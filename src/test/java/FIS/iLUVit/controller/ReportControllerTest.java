package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ReportCreateRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.domain.reports.Report;
import FIS.iLUVit.domain.reports.ReportDetail;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ReportService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @InjectMocks
    private ReportController target;
    @Mock
    private ReportService reportService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    Teacher targetUser, user;
    Post post;
    Comment comment;
    Report reportPost, reportComment;
    ReportDetail reportDetailPost, reportDetailComment;

    ReportCreateRequest reportCreateRequestPost = new ReportCreateRequest();
    ReportCreateRequest reportCreateRequestComment = new ReportCreateRequest();

    @BeforeEach
    public void init(){
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        targetUser = Creator.createTeacher(1L);
        user = Creator.createTeacher(2L);

        post = Creator.createPost(3L, "title", "content", false, null, targetUser);
        comment = Creator.createComment(4L, false, "content", post, targetUser);

        reportPost = Creator.createReport(5L, post.getId());
        reportComment = Creator.createReport(6L, comment.getId());

        reportDetailPost = Creator.createReportDetailPost(7L, user, post);
        reportDetailComment = Creator.createReportDetailComment(8L, user, comment);

        reportCreateRequestPost.setType(ReportType.POST);
        reportCreateRequestPost.setTargetId(post.getId());
        reportCreateRequestPost.setReason(ReportReason.REPORT_A);

        reportCreateRequestComment.setType(ReportType.COMMENT);
        reportCreateRequestComment.setTargetId(comment.getId());
        reportCreateRequestComment.setReason(ReportReason.REPORT_A);
    }


    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 신고_실패_유저토큰무효() throws Exception{
        //given
        String url = "/report";

        UserErrorResult errorResult = UserErrorResult.NOT_VALID_TOKEN;

        doThrow(new UserException(errorResult))
                .when(reportService)
                .registerReport(null, reportCreateRequestPost);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestPost))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }

    @Test
    public void 신고_실패_유저존재안함() throws Exception{
        //given
        String url = "/report";

        UserErrorResult errorResult = UserErrorResult.USER_NOT_EXIST;

        doThrow(new UserException(errorResult))
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestPost);
        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andDo(print())
                .andExpect(status().isBadRequest()) //418이 아니라 400인건지
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }


    @Test
    public void 게시글신고_실패_게시글존재안함() throws Exception {
        //given
        String url = "/report";

        PostErrorResult errorResult = PostErrorResult.POST_NOT_EXIST;

        doThrow(new PostException(errorResult))
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestPost);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }

    @Test
    public void 게시글신고_실패_중복신고() throws Exception{
        //given
        String url = "/report";

        ReportErrorResult errorResult = ReportErrorResult.ALREADY_EXIST_POST_REPORT;

        doThrow(new ReportException(errorResult))
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestPost);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );


        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }

    @Test
    public void 게시글신고_성공() throws Exception{
        //given
        String url = "/report";

        doReturn(reportDetailPost.getId())
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestPost);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        reportDetailPost.getId()
                )));
    }

    @Test
    public void 댓글신고_실패_댓글존재안함() throws Exception{
        //given
        String url = "/report";

        CommentErrorResult errorResult = CommentErrorResult.NO_EXIST_COMMENT;

        doThrow(new CommentException(errorResult))
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestComment);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestComment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }

    @Test
    public void 댓글신고_실패_중복신고() throws Exception{
        //given
        String url = "/report";

        ReportErrorResult errorResult = ReportErrorResult.ALREADY_EXIST_POST_REPORT;

        doThrow(new ReportException(errorResult))
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestComment);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestComment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                )));
    }

    @Test
    public void 댓글신고_성공() throws Exception{
        //given
        String url = "/report";

        doReturn(reportDetailComment.getId())
                .when(reportService)
                .registerReport(user.getId(), reportCreateRequestComment);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(reportCreateRequestComment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", createJwtToken(user))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        reportDetailComment.getId()
                )));
    }

}