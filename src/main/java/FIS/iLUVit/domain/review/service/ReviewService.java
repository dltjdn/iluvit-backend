package FIS.iLUVit.domain.review.service;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.review.domain.Review;
import FIS.iLUVit.domain.review.exception.ReviewErrorResult;
import FIS.iLUVit.domain.review.exception.ReviewException;
import FIS.iLUVit.domain.review.repository.ReviewRepository;
import FIS.iLUVit.domain.review.dto.*;
import FIS.iLUVit.domain.reviewheart.repository.ReviewHeartRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.center.domain.Score;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.common.service.ImageService;
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
        Center findCenter = getCenter(centerId);

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
        Parent parent = getParent(userId);

        Slice<Review> reviews = reviewRepository.findByParent(parent, pageable);

        Slice<ReviewByParentResponse> reviewDtoSlice = reviews
                .map(ReviewByParentResponse::new);

        return reviewDtoSlice;
    }

    /**
     * 리뷰를 등록합니다
     */
    public void saveNewReview(Long userId, ReviewCreateRequest reviewCreateRequest) {

        Parent findUser = getParent(userId);

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
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }

        Center findCenter = getCenter(reviewCreateRequest.getCenterId());

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
        Review findReview = getReview(reviewId);
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }
        findReview.updateContent(reviewContentRequest.getContent());
    }

    /**
     * 리뷰를 삭제합니다
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review findReview = getReview(reviewId);
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }
        reviewRepository.delete(findReview);
    }

    /**
     * 리뷰의 답글을 등록합니다
     */
    public void saveComment(Long reviewId, ReviewCommentRequest reviewCommentRequest, Long teacherId) {

        Review review = getReview(reviewId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        if (!teacher.getApproval().equals(Approval.ACCEPT)) {
            throw new ReviewException(ReviewErrorResult.APPROVAL_INCOMPLETE);
        }
        if (teacher.getAuth() != Auth.DIRECTOR) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }
        if (!Objects.equals(teacher.getCenter().getId(), review.getCenter().getId())) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }

        review.updateAnswer(reviewCommentRequest.getComment(), teacher);
    }

    /**
     * 리뷰의 답글을 삭제합니다
     */
    public void deleteComment(Long reviewId, Long teacherId) {
        Review review = getReview(reviewId);
        if (!Objects.equals(review.getTeacher().getId(), teacherId)) {
            throw new ReviewException(ReviewErrorResult.FORBIDDEN_ACCESS);
        }
        review.updateAnswer(null, null); // 대댓글 삭제 -> null
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        return centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 리뷰인가
     */
    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
    }
}
