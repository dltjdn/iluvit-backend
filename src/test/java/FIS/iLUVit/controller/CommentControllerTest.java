package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.comment.CommentDto;
import FIS.iLUVit.dto.comment.CommentRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.CommentErrorResult;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.exception.PostErrorResult;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CommentHeartService;
import FIS.iLUVit.service.CommentService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @InjectMocks
    CommentController commentController;

    @Mock
    CommentService commentService;
    @Mock
    CommentHeartService commentHeartService;

    Board board1;
    Post post1;
    User user1;
    Comment comment1;
    Comment comment2;
    Comment comment3;
    Comment comment4;
    CommentHeart commentHeart1;
    CommentRequest request = new CommentRequest();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        user1 = Parent.builder()
                .id(1L)
                .name("나")
                .auth(Auth.PARENT)
                .build();
        board1 = CreateTest.createBoard(2L, "자유게시판", BoardKind.NORMAL, null, true);
        post1 = Creator.createPost(3L,"제목", "내용", true, board1, user1);
        comment1 = Creator.createComment(4L,true, "안녕", post1, user1);
        comment2 = Creator.createComment(5L,true, "하세", post1, user1);
        comment3 = Creator.createComment(6L,true, "요", post1, user1);
        comment4 = Creator.createComment(7L,true, "ㅋㅋ", post1, user1);
        commentHeart1 = Creator.createCommentHeart(8L, user1, comment1);
    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user1.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 댓글_작성_비회원_접근() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("하이");

        final String url = "/user/comment";
        final CommentErrorResult error = CommentErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new CommentException(error))
                .when(commentService)
                .registerComment(null, post1.getId(), null, request);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", (String) null)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 댓글_작성_유저X() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 댓글_작성_게시글X() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("하이");

        final String url = "/user/comment";
        final PostErrorResult error = PostErrorResult.POST_NOT_EXIST;

        Mockito.doThrow(new PostException(error))
                .when(commentService)
                .registerComment(user1.getId(), post1.getId(), null, request);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", (String) null)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 댓글_작성_성공() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("안녕");

        final String url = "/user/comment";

        Mockito.doReturn(comment1.getId())
                .when(commentService)
                .registerComment(user1.getId(), post1.getId(), null, request);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", (String) null)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        comment1.getId()
                )));
    }

    @Test
    public void 대댓글_작성_성공() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("안녕");

        final String url = "/user/comment";

        Mockito.doReturn(comment1.getId())
                .when(commentService)
                .registerComment(user1.getId(), post1.getId(), comment2.getId(), request);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", comment2.getId().toString())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        comment1.getId()
                )));
    }

    @Test
    public void 댓글_삭제_비회원_혹은_접근_권한_제한() throws Exception {
        //given

        final String url = "/user/comment";
        final CommentErrorResult error = CommentErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new CommentException(error))
                .when(commentService)
                .deleteComment(null, comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", comment1.getId().toString())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 댓글_삭제_댓글X() throws Exception {
        //given

        final String url = "/user/comment";
        final CommentErrorResult error = CommentErrorResult.NO_EXIST_COMMENT;

        Mockito.doThrow(new CommentException(error))
                .when(commentService)
                .deleteComment(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .header("Authorization", createJwtToken())
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", comment1.getId().toString())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 댓글_삭제_성공() throws Exception {
        //given

        final String url = "/user/comment";

        Mockito.doReturn(comment1.getId())
                .when(commentService)
                .deleteComment(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .header("Authorization", createJwtToken())
                        .param("post_id", post1.getId().toString())
                        .param("comment_id", comment1.getId().toString())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        comment1.getId()
                )));
    }

    @Test
    public void 댓글_좋아요_비회원() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";
        final CommentErrorResult error = CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART;

        Mockito.doThrow(new CommentException(error))
                .when(commentHeartService)
                .save(null, comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, comment1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 댓글_좋아요_이미_존재() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";
        final CommentErrorResult error = CommentErrorResult.ALREADY_EXIST_HEART;

        Mockito.doThrow(new CommentException(error))
                .when(commentHeartService)
                .save(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, comment1.getId())
                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 댓글_좋아요_성공() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";

        Mockito.doReturn(commentHeart1.getId())
                .when(commentHeartService)
                .save(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, comment1.getId())
                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        commentHeart1.getId()
                )));

    }

    @Test
    public void 댓글_좋아요_취소_비회원_혹은_권한X() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";


        CommentErrorResult error = CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART;
        Mockito.doThrow(new CommentException(error))
                .when(commentHeartService)
                .delete(null, comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, comment1.getId())
//                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 댓글_좋아요_취소_좋아요X() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";


        CommentErrorResult error = CommentErrorResult.NO_EXIST_COMMENT_HEART;
        Mockito.doThrow(new CommentException(error))
                .when(commentHeartService)
                .delete(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, comment1.getId())
                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 댓글_좋아요_취소_성공() throws Exception {
        //given
        final String url = "/user/commentHeart/comment/{comment_id}";

        Mockito.doReturn(commentHeart1.getId())
                .when(commentHeartService)
                .delete(user1.getId(), comment1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, comment1.getId())
                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        commentHeart1.getId()
                )));

    }

    @Test
    public void 댓글_단_리스트_조회() throws Exception {
        //given
        final String url = "/user/comment/mypage";

        CommentDto commentDto1 = new CommentDto(comment1);
        CommentDto commentDto2 = new CommentDto(comment2);
        CommentDto commentDto3 = new CommentDto(comment3);
        CommentDto commentDto4 = new CommentDto(comment4);
        Slice<CommentDto> commentSlice = new SliceImpl<>(
                Arrays.asList(commentDto1, commentDto2, commentDto3, commentDto4));

        Mockito.doReturn(commentSlice)
                .when(commentService)
                .searchByUser(user1.getId(), PageRequest.of(0, 10));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", createJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        commentSlice
                )));
    }


}