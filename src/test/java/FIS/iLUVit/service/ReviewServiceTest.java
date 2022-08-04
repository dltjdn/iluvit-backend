package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.ReviewByCenterDTO;
import FIS.iLUVit.controller.dto.ReviewByParentDTO;
import FIS.iLUVit.controller.dto.ReviewCreateDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ReviewRepository;
import FIS.iLUVit.repository.TeacherRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    TeacherRepository teacherRepository;
    @Mock
    ParentRepository parentRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    ImageService imageService;

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

    ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO();
    ReviewByCenterDTO reviewByCenterDTO = new ReviewByCenterDTO();

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

    }

    @Test
    public void 학부모로_리뷰_찾기() throws Exception {
        //given
        ReviewByParentDTO expected = new ReviewByParentDTO();
        ReviewByParentDTO.ReviewDto reviewDto = new ReviewByParentDTO.ReviewDto(review1);
        System.out.println("reviewDto = " + reviewDto);
        List<ReviewByParentDTO.ReviewDto> reviewDtos = List.of(reviewDto);
        SliceImpl<ReviewByParentDTO.ReviewDto> reviewDtoSlice = new SliceImpl<>(reviewDtos, PageRequest.of(0, 10), false);
        expected.setReviews(reviewDtoSlice);

        List<Review> reviewList = List.of(review1);
        SliceImpl<Review> reviewSlice = new SliceImpl<>(reviewList, PageRequest.of(0, 10), false);
        Mockito.doReturn(reviewSlice)
                .when(reviewRepository)
                .findByParent(parent1.getId(), PageRequest.of(0, 10));
        //when
        ReviewByParentDTO result = reviewService.findByParent(parent1.getId(), PageRequest.of(0, 10));
        //then
        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(expected));
    }
    
    @Test
    public void 리뷰_등록_비회원() throws Exception {
        //given

        //when
        assertThatThrownBy(
                () -> reviewService.saveReview(null, null)
        ).isInstanceOf(UserException.class);
        //then

    }

    @Test
    public void 리뷰_등록_학부모X() throws Exception {
        //given
        reviewCreateDTO.setAnonymous(true);
        reviewCreateDTO.setCenterId(center1.getId());
        reviewCreateDTO.setContent("위생에 철저해요");
        reviewCreateDTO.setScore(5);

        Mockito.doReturn(Optional.empty())
                .when(parentRepository)
                .findWithChildren(parent1.getId());
        //when
        //then
        assertThatThrownBy(
                () -> reviewService.saveReview(parent1.getId(), reviewCreateDTO)
        ).isInstanceOf(UserException.class);
    }

    @Test
    public void 리뷰_등록_학부모의_아이가_센터에_속해있지_않음() throws Exception {
        //given
        reviewCreateDTO.setAnonymous(true);
        reviewCreateDTO.setCenterId(center1.getId());
        reviewCreateDTO.setContent("위생에 철저해요");
        reviewCreateDTO.setScore(5);

        Mockito.doReturn(Optional.of(parent2))
                .when(parentRepository)
                .findWithChildren(parent2.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveReview(parent2.getId(), reviewCreateDTO));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_등록_센터X() throws Exception {
        //given
        reviewCreateDTO.setAnonymous(true);
        reviewCreateDTO.setCenterId(center1.getId());
        reviewCreateDTO.setContent("위생에 철저해요");
        reviewCreateDTO.setScore(5);

        Mockito.doReturn(Optional.of(parent1))
                .when(parentRepository)
                .findWithChildren(parent1.getId());

        Mockito.doReturn(Optional.empty())
                .when(centerRepository)
                .findById(center1.getId());
        //when

        CenterException result = assertThrows(CenterException.class,
                () -> reviewService.saveReview(parent1.getId(), reviewCreateDTO));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CenterErrorResult.CENTER_NOT_EXIST);
    }

    @Test
    public void 리뷰_등록_2개_이상_시도() throws Exception {
        //given
        reviewCreateDTO.setAnonymous(true);
        reviewCreateDTO.setCenterId(center1.getId());
        reviewCreateDTO.setContent("위생에 철저해요");
        reviewCreateDTO.setScore(5);

        Mockito.doReturn(Optional.of(parent1))
                .when(parentRepository)
                .findWithChildren(parent1.getId());

        Mockito.doReturn(Optional.of(center1))
                .when(centerRepository)
                .findById(center1.getId());

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findByUserAndCenter(parent1.getId(), reviewCreateDTO.getCenterId());
        //when

        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveReview(parent1.getId(), reviewCreateDTO));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW);
    }

    @Test
    public void 리뷰_등록_성공() throws Exception {
        //given
        reviewCreateDTO.setAnonymous(true);
        reviewCreateDTO.setCenterId(center1.getId());
        reviewCreateDTO.setContent("위생에 철저해요");
        reviewCreateDTO.setScore(5);

        Mockito.doReturn(Optional.of(parent1))
                .when(parentRepository)
                .findWithChildren(parent1.getId());

        Mockito.doReturn(Optional.of(center1))
                .when(centerRepository)
                .findById(center1.getId());

        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findByUserAndCenter(parent1.getId(), reviewCreateDTO.getCenterId());

        Mockito.doReturn(review1)
                .when(reviewRepository)
                .save(any());
        //when

        Long savedId = reviewService.saveReview(parent1.getId(), reviewCreateDTO);
        //then
        assertThat(savedId)
                .isEqualTo(review1.getId());
    }

    @Test
    public void 리뷰_수정_리뷰X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findById(review1.getId());

        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.updateReview(review1.getId(), parent1.getId(), "수정사항"));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_NOT_EXIST);
    }

    @Test
    public void 리뷰_수정_비회원() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.updateReview(review1.getId(), null, "수정사항"));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_수정_권한X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review2))
                .when(reviewRepository)
                .findById(review2.getId());

        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.updateReview(review2.getId(), parent1.getId(), "수정사항"));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_수정_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        //when
        reviewService.updateReview(review1.getId(), parent1.getId(), "수정사항");

        //then
        assertThat(review1.getContent())
                .isEqualTo("수정사항");
    }

    @Test
    public void 리뷰_삭제_리뷰X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findById(review1.getId());

        //when

        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.deleteReview(review1.getId(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_NOT_EXIST);
    }

    @Test
    public void 리뷰_삭제_권한X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review2))
                .when(reviewRepository)
                .findById(review2.getId());

        //when

        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.deleteReview(review2.getId(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_삭제_성공() throws Exception {
        //given

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doNothing()
                .when(reviewRepository)
                .delete(review1);

        //when

        reviewService.deleteReview(review1.getId(), parent1.getId());

        //then

    }

    @Test
    public void 센터로_리뷰_리스트_조회() throws Exception {
        //given
        String imagePath = "/Desktop/User";
        ReviewByCenterDTO.ReviewCenterDto reviewCenterDto1 = getReviewCenterDto(review1, imagePath, teacher1.getId());
        ReviewByCenterDTO.ReviewCenterDto reviewCenterDto2 = getReviewCenterDto(review2, imagePath, null);
        ReviewByCenterDTO.ReviewCenterDto reviewCenterDto3 = getReviewCenterDto(review3, imagePath, null);
        List<ReviewByCenterDTO.ReviewCenterDto> reviewCenterDtos = Arrays.asList(reviewCenterDto1, reviewCenterDto2, reviewCenterDto3);
        Slice<ReviewByCenterDTO.ReviewCenterDto> dtoSlice = new SliceImpl<>(reviewCenterDtos);
        reviewByCenterDTO.setReviews(dtoSlice);

        List<Review> reviewList = Arrays.asList(review1, review2, review3);
        Slice<Review> reviewSlice = new SliceImpl<>(reviewList);
        Mockito.doReturn(reviewSlice)
                .when(reviewRepository)
                .findByCenter(center1.getId(), PageRequest.of(0, 10));

        Mockito.doReturn(imagePath)
                .when(imageService)
                .getProfileImage(any(BaseImageEntity.class));

        //when
        ReviewByCenterDTO result = reviewService
                .findByCenter(center1.getId(), PageRequest.of(0, 10));

        //then
        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(reviewByCenterDTO));
    }

    @NotNull
    private ReviewByCenterDTO.ReviewCenterDto getReviewCenterDto(Review review, String imagePath, Long teacherId) {
        return new ReviewByCenterDTO.ReviewCenterDto(review.getId(), review.getParent().getId(),
                review.getParent().getNickName(), review.getContent(), review.getScore(),
                review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(),
                review.getUpdateTime(), teacherId, review.getAnswer(),
                review.getAnswerCreateDate(), review.getAnswerCreateTime(),
                review.getAnonymous(), 0, imagePath);
    }

    @Test
    public void 리뷰_답글_작성_리뷰X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findById(review1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveComment(
                        review1.getId(), "리뷰 남겨주셔서 감사합니다", teacher1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_NOT_EXIST);
    }

    @Test
    public void 리뷰_답글_작성_교사X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doReturn(Optional.empty())
                .when(teacherRepository)
                .findById(teacher1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> reviewService.saveComment(
                        review1.getId(), "리뷰 남겨주셔서 감사합니다", teacher1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 리뷰_답글_작성_교사_승인X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveComment(
                        review1.getId(), "리뷰 남겨주셔서 감사합니다", teacher1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.APPROVAL_INCOMPLETE);
    }

    @Test
    public void 리뷰_답글_작성_교사_승인O_디렉터X() throws Exception {
        //given

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doReturn(Optional.of(teacher2))
                .when(teacherRepository)
                .findById(teacher2.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveComment(
                        review1.getId(), "리뷰 남겨주셔서 감사합니다", teacher2.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_답글_작성_교사_승인O_디렉터O_해당_센터_교사X() throws Exception {
        //given
        Center center2 = Creator.createCenter(55L, "핑핑유치원", true, true, null);

        Teacher teacher3 = Teacher.builder()
                .id(99L)
                .center(center2)
                .approval(Approval.ACCEPT)
                .name("TeacherX")
                .auth(Auth.DIRECTOR)
                .build();

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());

        Mockito.doReturn(Optional.of(teacher3))
                .when(teacherRepository)
                .findById(teacher3.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.saveComment(
                        review1.getId(), "리뷰 남겨주셔서 감사합니다", teacher3.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_답글_작성_성공() throws Exception {
        //given
        teacher2.beDirector();

        Mockito.doReturn(Optional.of(review2))
                .when(reviewRepository)
                .findById(review2.getId());

        Mockito.doReturn(Optional.of(teacher2))
                .when(teacherRepository)
                .findById(teacher2.getId());

        //when
        Long savedId = reviewService
                .saveComment(review2.getId(), "리뷰 남겨주셔서 감사합니다", teacher2.getId());
        //then
        assertThat(savedId)
                .isEqualTo(review2.getId());
        assertThat(review2.getAnswer())
                .isEqualTo("리뷰 남겨주셔서 감사합니다");
        assertThat(review2.getTeacher())
                .isEqualTo(teacher2);

    }

    @Test
    public void 리뷰_답글_삭제_리뷰X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(reviewRepository)
                .findById(review1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.deleteComment(review1.getId(), teacher1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.REVIEW_NOT_EXIST);
    }

    @Test
    public void 리뷰_답글_삭제_권한X() throws Exception {
        //given
        Center center2 = Creator.createCenter(55L, "핑핑유치원", true, true, null);

        Teacher teacher3 = Teacher.builder()
                .id(99L)
                .center(center2)
                .approval(Approval.ACCEPT)
                .name("TeacherX")
                .auth(Auth.DIRECTOR)
                .build();

        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());
        //when
        ReviewException result = assertThrows(ReviewException.class,
                () -> reviewService.deleteComment(review1.getId(), teacher3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 리뷰_답글_삭제_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(review1))
                .when(reviewRepository)
                .findById(review1.getId());
        //when
        reviewService.deleteComment(review1.getId(), teacher1.getId());

        //then
        assertThat(review1.getAnswer())
                .isNull();
        assertThat(review1.getTeacher())
                .isNull();

    }

}