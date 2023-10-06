package FIS.iLUVit.service;

import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.ReviewHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ReviewErrorResult;
import FIS.iLUVit.exception.ReviewException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ReviewHeartRepository;
import FIS.iLUVit.repository.ReviewRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewHeartService {
    private final ReviewHeartRepository reviewHeartRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public void saveReviewHeart(Long reviewId, Long userId) {

        Review review = getReview(reviewId);
        User user = getUser(userId);

        reviewHeartRepository.findByReviewAndUser(review, user)
                .ifPresent(existingReviewHeart -> {
                    throw new ReviewException(ReviewErrorResult.NO_MORE_THAN_ONE_REVIEW_HEART);
                });
        ReviewHeart reviewHeart = new ReviewHeart(review, user);

        reviewHeartRepository.save(reviewHeart);
    }


    public void deleteReviewHeart(Long reviewId, Long userId) {
        Review review = getReview(reviewId);
        User user = getUser(userId);

        reviewHeartRepository.findByReviewAndUser(review, user)
                .ifPresentOrElse(reviewHeartRepository::delete, () -> {
                    throw new ReviewException(ReviewErrorResult.REVIEW_HEART_NOT_FOUND);
                });
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 리뷰인가
     */
    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorResult.REVIEW_NOT_FOUND));
    }
}
