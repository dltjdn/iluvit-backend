package FIS.iLUVit.service;

import FIS.iLUVit.dto.review.ReviewByCenterDto;
import FIS.iLUVit.dto.review.ReviewByParentDto;
import FIS.iLUVit.dto.review.ReviewDetailDto;
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
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;

    public Slice<ReviewByParentDto> findReviewListByParent(Long userId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.findByParent(userId, pageable);

        Slice<ReviewByParentDto> reviewDtoSlice = reviews
                .map(review -> new ReviewByParentDto(review));

        return reviewDtoSlice;
    }

    public Long saveNewReview(Long userId, ReviewDetailDto reviewCreateDTO) {

        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        Parent findUser = parentRepository.findWithChildren(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 리뷰_등록_학부모의_아이가_센터에_속해있지_않음
        List<Child> children = findUser.getChildren();
        boolean flag = false;
        for (Child child : children) {
            if (child.getCenter() != null) {
                if (Objects.equals(child.getCenter().getId(), reviewCreateDTO.getCenterId())) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        Center findCenter = centerRepository.findById(reviewCreateDTO.getCenterId())
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 유저가 시설에 작성한 리뷰가 이미 존재하는 지 검증
        reviewRepository.findByUserAndCenter(userId, reviewCreateDTO.getCenterId())
                .ifPresent((r) -> {
                    log.info("r.getId : " + r.getId().toString());
                    throw new ReviewException(ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW);
                });

        Review review = Review.createReview(reviewCreateDTO.getContent(), reviewCreateDTO.getScore(),
                reviewCreateDTO.getAnonymous(), findUser, findCenter);
        findCenter.addScore(Score.Review); // 리뷰 작성 시 센터의 스코어 올림
        return reviewRepository.save(review).getId();
    }

    public void modifyReview(Long reviewId, Long userId, String content) {
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException(ReviewErrorResult.REVIEW_NOT_EXIST));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        findReview.updateContent(content);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_EXIST));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        reviewRepository.delete(findReview);
    }

    public Slice<ReviewByCenterDto> findReviewByCenter(Long centerId, Pageable pageable) {
        // getParent 지연 로딩 쿼리 막음
        Slice<Review> reviews = reviewRepository.findByCenter(centerId, pageable);

        Slice<ReviewByCenterDto> reviewByCenterDtos = reviews.map(review -> {

            Integer like = review.getReviewHearts().size();
            Long teacherId = review.getTeacher() == null ? null : review.getTeacher().getId();

            return new ReviewByCenterDto(
                    review.getId(), review.getParent().getId(), review.getParent().getNickName(), review.getContent(), review.getScore(),
                    review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(), review.getUpdateTime(),
                    teacherId, review.getAnswer(), review.getAnswerCreateDate(), review.getAnswerCreateTime(),
                    review.getAnonymous(), like, imageService.getProfileImage(review.getParent())
            );
        });

        return reviewByCenterDtos;
    }

    public Long saveComment(Long reviewId, String comment, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_EXIST));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        log.info("teacher.getCenter() : " + teacher.getCenter().toString());
        log.info("review.getCenter() : " + review.getCenter().toString());
        if (!teacher.getApproval().equals(Approval.ACCEPT)) {
            throw new ReviewException(ReviewErrorResult.APPROVAL_INCOMPLETE);
        }
        if (teacher.getAuth() != Auth.DIRECTOR) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        if (!Objects.equals(teacher.getCenter().getId(), review.getCenter().getId())) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        review.updateAnswer(comment, teacher);
        return reviewId;
    }

    public void deleteComment(Long reviewId, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_EXIST));
        if (!Objects.equals(review.getTeacher().getId(), teacherId)) {
            throw new ReviewException(ReviewErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        review.updateAnswer(null, null); // 대댓글 삭제 -> null
    }
}
