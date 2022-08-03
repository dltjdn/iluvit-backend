package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ReviewByCenterDTO;
import FIS.iLUVit.controller.dto.ReviewCommentDTO;
import FIS.iLUVit.controller.dto.ReviewCreateDTO;
import FIS.iLUVit.controller.dto.ReviewUpdateDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.ReviewErrorResult;
import FIS.iLUVit.exception.ReviewException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ReviewHeartService;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewHeartControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    ReviewHeartController reviewHeartController;

    @Mock
    ReviewHeartService reviewHeartService;

    ObjectMapper objectMapper;
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

    ReviewHeart reviewHeart1;
    ReviewHeart reviewHeart2;
    ReviewHeart reviewHeart3;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewHeartController)
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

        reviewHeart1 = new ReviewHeart(10L, review1, parent1);
        reviewHeart2 = new ReviewHeart(11L, review1, parent2);
        reviewHeart3 = new ReviewHeart(12L, review1, parent3);
    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 리뷰_좋아요_비회원() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        ReviewErrorResult error = ReviewErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewHeartService)
                .saveReviewHeart(review1.getId(), null);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_좋아요_중복_등록() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        ReviewErrorResult error = ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW_HEART;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewHeartService)
                .saveReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_좋아요_리뷰X() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_NOT_EXIST;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewHeartService)
                .saveReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_좋아요_유저X() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;
        Mockito.doThrow(new UserException(error))
                .when(reviewHeartService)
                .saveReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 리뷰_좋아요_성공() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        Mockito.doReturn(reviewHeart1.getId())
                .when(reviewHeartService)
                .saveReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        reviewHeart1.getId()
                )));
    }

    @Test
    public void 리뷰_삭제_좋아요X() throws Exception {
        //given
        String url = "/user/reviewHeart/review/{review_id}";
        ReviewErrorResult error = ReviewErrorResult.REVIEW_HEART_NOT_EXIST;
        Mockito.doThrow(new ReviewException(error))
                .when(reviewHeartService)
                .deleteReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
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
        String url = "/user/reviewHeart/review/{review_id}";
        Mockito.doNothing()
                .when(reviewHeartService)
                .deleteReviewHeart(review1.getId(), parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, review1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }
}