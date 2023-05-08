package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ReviewService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @Mock
    ReviewService reviewService;

    @InjectMocks
    ReviewController reviewController;

    Child child1;
    Child child2;
    Child child3;

    Parent parent1;
    Parent parent2;
    Parent parent3;

    Teacher teacher1;
    Teacher teacher2;

    Center center1;

    Review review1;
    Review review2;
    Review review3;

    ReviewDetailDto reviewDetailDto;
    ReviewDto reviewDto;
    ReviewCommentDto reviewCommentDto;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        center1 = Creator.createCenter(5L, "팡팡유치원", true, true, null);

        child1 = Child.createChild("childA", null, null, parent1);
        child2 = Child.createChild("childB", null, null, parent1);
        child3 = Child.createChild("childC", null, null, parent1);
        child1.mappingCenter(center1);

        parent1 = Parent.builder()
                .id(1L)
                .name("ParentA")
                .auth(Auth.PARENT)
                .build();
        parent1.getChildren().add(child1);
        parent1.getChildren().add(child2);
        parent1.getChildren().add(child3);

        parent2 = Parent.builder()
                .id(2L)
                .name("ParentB")
                .auth(Auth.PARENT)
                .build();
        parent3 = Parent.builder()
                .id(3L)
                .name("ParentC")
                .auth(Auth.PARENT)
                .build();
        teacher1 = Teacher.builder()
                .id(4L)
                .center(center1)
                .approval(Approval.WAITING)
                .name("TeacherA")
                .auth(Auth.TEACHER)
                .build();
        teacher2 = Teacher.builder()
                .id(9L)
                .center(center1)
                .approval(Approval.ACCEPT)
                .name("TeacherB")
                .auth(Auth.TEACHER)
                .build();

        review1 = Creator.createReview(6L, center1, 5, parent1, null, "위생에 철저해요");
        review1.updateAnswer("리뷰 남겨주셔서 감사합니다", teacher1);
        review2 = Creator.createReview(7L, center1, 4, parent2, null, "나쁘지 않아요");
        review3 = Creator.createReview(8L, center1, 1, parent3, null, "불친절해요");

    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 학부모가_작성한_리뷰_조회() throws Exception {
        //given
        ReviewByParentDto reviewDto = new ReviewByParentDto(review1);
        List<ReviewByParentDto> reviewDtos = List.of(reviewDto);
        SliceImpl<ReviewByParentDto> reviewByParentSlice = new SliceImpl<>(reviewDtos, PageRequest.of(0, 10), false);


        Mockito.doReturn(reviewByParentSlice)
                .when(reviewService)
                .findByParent(parent1.getId(), PageRequest.of(0, 10));

        final String url = "/review";
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(reviewByParentSlice)
                ));
    }

    @Test
    public void 리뷰_등록_토큰X() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);

        final String url = "/review";

        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;
        Mockito.doThrow(new UserException(error))
                .when(reviewService)
                .saveReview(null, reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        )));

    }

    @Test
    public void 리뷰_등록_유저X() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);

        final String url = "/review";

        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;
        Mockito.doThrow(new UserException(error))
                .when(reviewService)
                .saveReview(parent1.getId(), reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(parent1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_등록_권한X() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);
        final String url = "/review";

        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .saveReview(parent1.getId(), reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(parent1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_등록_센터X() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);

        final String url = "/review";

        CenterErrorResult error = CenterErrorResult.CENTER_NOT_EXIST;
        Mockito.doThrow(new CenterException(error))
                .when(reviewService)
                .saveReview(parent1.getId(), reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(parent1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_등록_같은_센터에_2개_이상() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);
        final String url = "/review";

        ReviewErrorResult error = ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .saveReview(parent1.getId(), reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(parent1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_등록_성공() throws Exception {
        //given
        reviewDetailDto = new ReviewDetailDto(center1.getId(),"위생에 철저해요",5, true);

        final String url = "/review";

        Mockito.doReturn(review1.getId())
                .when(reviewService)
                .saveReview(parent1.getId(), reviewDetailDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(parent1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailDto))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        review1.getId()
                )));
    }

    @Test
    public void 리뷰_수정_리뷰X() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .updateReview(review1.getId(), parent1.getId(), "수정했어요");
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
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
    public void 리뷰_수정_권한X() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .updateReview(review1.getId(), parent1.getId(), "수정했어요");
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
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
    public void 리뷰_수정_성공() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doNothing()
                .when(reviewService)
                .updateReview(review1.getId(), parent1.getId(), "수정했어요");
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 리뷰_삭제_리뷰X() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .deleteReview(review1.getId(), parent1.getId());
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
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
    public void 리뷰_삭제_권한X() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .deleteReview(review1.getId(), parent1.getId());
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
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
    public void 리뷰_삭제_성공() throws Exception {
        //given
        final String url = "/review/{reviewId}";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;
        Mockito.doNothing()
                .when(reviewService)
                .deleteReview(review1.getId(), parent1.getId());
        reviewDto = new ReviewDto("수정했어요");
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
                        .content(objectMapper.writeValueAsString(reviewDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @NotNull
    private ReviewByCenterDto getReviewCenterDto(Review review, String imagePath, Long teacherId) {
        return new ReviewByCenterDto(review.getId(), review.getParent().getId(),
                review.getParent().getNickName(), review.getContent(), review.getScore(),
                review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(),
                review.getUpdateTime(), teacherId, review.getAnswer(),
                review.getAnswerCreateDate(), review.getAnswerCreateTime(),
                review.getAnonymous(), 0, imagePath);
    }
    @Test
    public void 센터에_올라온_리뷰들_조회() throws Exception {
        //given
        String imagePath = "/Desktop/User";
        ReviewByCenterDto reviewCenterDto1 = getReviewCenterDto(review1, imagePath, teacher1.getId());
        ReviewByCenterDto reviewCenterDto2 = getReviewCenterDto(review2, imagePath, null);
        ReviewByCenterDto reviewCenterDto3 = getReviewCenterDto(review3, imagePath, null);
        List<ReviewByCenterDto> reviewCenterList = Arrays.asList(reviewCenterDto1, reviewCenterDto2, reviewCenterDto3);
        Slice<ReviewByCenterDto> reviewByCenterDtos = new SliceImpl<>(reviewCenterList);

        Mockito.doReturn(reviewByCenterDtos)
                .when(reviewService)
                .findByCenter(center1.getId(), PageRequest.of(0, 10));

        final String url = "/review/center/{centerId}";
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, center1.getId())
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        objectMapper.writeValueAsString(reviewByCenterDtos)
                ));

    }

    @Test
    public void 시설_리뷰_답글_등록_수정_리뷰X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;

        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .saveComment(review1.getId(), "리뷰를 남겨주셔서 감사해요", teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCommentDto))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 시설_리뷰_답글_등록_수정_유저X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new UserException(error))
                .when(reviewService)
                .saveComment(review1.getId(), "리뷰를 남겨주셔서 감사해요", teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCommentDto))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 시설_리뷰_답글_등록_수정_승인X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";
        ReviewErrorResult error = ReviewErrorResult.APPROVAL_INCOMPLETE;

        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .saveComment(review1.getId(), "리뷰를 남겨주셔서 감사해요", teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCommentDto))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 시설_리뷰_답글_등록_수정_권한X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";
        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .saveComment(review1.getId(), "리뷰를 남겨주셔서 감사해요", teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCommentDto))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 시설_리뷰_답글_등록_수정_성공() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";

        Mockito.doReturn(review1.getId())
                .when(reviewService)
                .saveComment(review1.getId(), "리뷰를 남겨주셔서 감사해요", teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCommentDto))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        review1.getId()
                )));
    }

    @Test
    public void 시설_리뷰_답글_삭제_리뷰X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";

        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .deleteComment(review1.getId(), teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 시설_리뷰_답글_삭제_권한X() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";

        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewService)
                .deleteComment(review1.getId(), teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 시설_리뷰_답글_삭제_성공() throws Exception {
        //given
        reviewCommentDto = new ReviewCommentDto("리뷰를 남겨주셔서 감사해요");

        final String url = "/review/{reviewId}/comment";

        Mockito.doNothing()
                .when(reviewService)
                .deleteComment(review1.getId(), teacher1.getId());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(teacher1))
        );
        //then

        resultActions.andDo(print())
                .andExpect(status().isOk());

    }



}