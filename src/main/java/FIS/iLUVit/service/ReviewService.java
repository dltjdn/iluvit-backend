package FIS.iLUVit.service;

import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewHeartRepository reviewHeartRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;

    /**
     * 해당 시설의 리뷰를 조회하여 조회된 리뷰 리스트를 dto를 반환합니다
     */
    public Slice<ReviewByCenterResponse> findReviewByCenter(Long centerId, Pageable pageable) {
        Center findCenter = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));

        Slice<Review> reviews = reviewRepository.findByCenterOrderByCreatedDate(findCenter, pageable);

        Slice<ReviewByCenterResponse> reviewByCenterDtos = reviews.map(review -> {

            int reviewHeartNum = reviewHeartRepository.findByReview(review).size();

            Long teacherId = review.getTeacher() == null ? null : review.getTeacher().getId();

            return new ReviewByCenterResponse(
                    review.getId(), review.getParent().getId(), review.getParent().getNickName(), review.getContent(), review.getScore(),
                    review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(), review.getUpdateTime(),
                    teacherId, review.getAnswer(), review.getAnswerCreateDate(), review.getAnswerCreateTime(),
                    review.getAnonymous(), reviewHeartNum, imageService.getProfileImage(review.getParent())
            );
        });

        return reviewByCenterDtos;
    }

    /**
     * 사용자가 작성한 리뷰 리스트를 조회하고 dto를 반환합니다
     */
    public Slice<ReviewByParentResponse> findReviewListByParent(Long userId, Pageable pageable) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Slice<Review> reviews = reviewRepository.findByParent(parent, pageable);

        Slice<ReviewByParentResponse> reviewDtoSlice = reviews
                .map(ReviewByParentResponse::new);

        return reviewDtoSlice;
    }

    /**
     * 리뷰를 등록합니다
     */
    public void saveNewReview(Long userId, ReviewCreateRequest reviewCreateRequest) {

        Parent findUser = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 리뷰_등록_학부모의_아이가_센터에_속해있지_않음
        List<Child> children = findUser.getChildren();
        boolean flag = false;
        for (Child child : children) {
            if (child.getCenter() != null) {
                if (Objects.equals(child.getCenter().getId(), reviewCreateRequest.getCenterId())) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }

        Center findCenter = centerRepository.findById(reviewCreateRequest.getCenterId())
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));

        findCenter.addScore(Score.Review); // 리뷰 작성 시 센터의 스코어 올림


        // 유저가 시설에 작성한 리뷰가 이미 존재하는 지 검증
        reviewRepository.findByParentAndCenter(findUser, findCenter)
                .ifPresent((existingReview) -> {
                    throw new ReviewException(ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW);
                });

        Review review = Review.createReview(reviewCreateRequest.getContent(), reviewCreateRequest.getScore(),
                reviewCreateRequest.getAnonymous(), findUser, findCenter);
        reviewRepository.save(review);
    }

    /**
     * 리뷰를 수정합니다
     */
    public void modifyReview(Long reviewId, Long userId, ReviewContentRequest reviewContentRequest) {
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }
        findReview.updateContent(reviewContentRequest.getContent());
    }

    /**
     * 리뷰를 삭제합니다
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }
        reviewRepository.delete(findReview);
    }

    /**
     * 리뷰의 답글을 등록합니다
     */
    public void saveComment(Long reviewId, ReviewCommentRequest reviewCommentRequest, Long teacherId) {
   
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        if (!teacher.getApproval().equals(Approval.ACCEPT)) {
            throw new ReviewException(ReviewErrorResult.APPROVAL_INCOMPLETE);
        }
        if (teacher.getAuth() != Auth.DIRECTOR) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }
        if (!Objects.equals(teacher.getCenter().getId(), review.getCenter().getId())) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }

        review.updateAnswer(reviewCommentRequest.getComment(), teacher);
    }

    /**
     * 리뷰의 답글을 삭제합니다
     */
    public void deleteComment(Long reviewId, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
        if (!Objects.equals(review.getTeacher().getId(), teacherId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_REVIEW_ACCESS);
        }
        review.updateAnswer(null, null); // 대댓글 삭제 -> null
    }
}
