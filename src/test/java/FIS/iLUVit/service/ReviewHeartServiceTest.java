package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.ReviewErrorResult;
import FIS.iLUVit.exception.ReviewException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ReviewHeartRepository;
import FIS.iLUVit.repository.ReviewRepository;
import FIS.iLUVit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReviewHeartServiceTest {

    @InjectMocks
    ReviewHeartService reviewHeartService;

    @Mock
    ReviewHeartRepository reviewHeartRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    UserRepository userRepository;

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

    @Test
    public void 좋아요_저장_비회원() throws Exception {
        //given

        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewHeartService.saveReviewHeart(review1.getId(), null));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);

    }

    @Test
    public void 좋아요_저장_같은_리뷰에_중복_시도() throws Exception {
        //given
        Mockito.doReturn(Optional.of(reviewHeart1))
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewHeartService.saveReviewHeart(review1.getId(), parent1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW_HEART);
    }

    @Test
    public void 좋아요_저장_리뷰X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());

        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findById(review1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewHeartService.saveReviewHeart(review1.getId(), parent1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_NOT_EXIST);
    }

    @Test
    public void 좋아요_저장_유저X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

//        Mockito.doReturn(Optional.of(parent1))
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> reviewHeartService.saveReviewHeart(review1.getId(), parent1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 좋아요_저장_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(reviewHeart1)
                .when(reviewHeartRepository)
                .save(any());
        //when
        Long savedId = reviewHeartService.saveReviewHeart(review1.getId(), parent1.getId());
        //then
        assertThat(savedId)
                .isEqualTo(reviewHeart1.getId());
    }

    @Test
    public void 좋아요_취소_좋아요X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewHeartService.deleteReviewHeart(review1.getId(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_HEART_NOT_EXIST);
    }

    @Test
    public void 좋아요_취소_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(reviewHeart1))
                .when(reviewHeartRepository)
                .findByReviewAndUser(review1.getId(), parent1.getId());

        Mockito.doNothing()
                .when(reviewHeartRepository)
                .delete(reviewHeart1);
        //when
        reviewHeartService.deleteReviewHeart(review1.getId(), parent1.getId());

        //then
    }

}