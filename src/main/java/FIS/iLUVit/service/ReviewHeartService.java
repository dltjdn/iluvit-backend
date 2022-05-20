package FIS.iLUVit.service;

import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.ReviewHeart;
import FIS.iLUVit.domain.User;
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
        reviewHeartRepository.findByReviewAndUser(reviewId, userId)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 좋아요한 리뷰에 좋아요 불가능");
                });
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 리뷰 아이디"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저 아이디"));
        ReviewHeart reviewHeart = new ReviewHeart(findReview, user);
        reviewHeartRepository.save(reviewHeart);
    }

    public void deleteReviewHeart(Long reviewId, Long userId) {
        ReviewHeart findReviewHeart = reviewHeartRepository.findByReviewAndUser(reviewId, userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 좋아요"));
        reviewHeartRepository.delete(findReviewHeart);
    }
}
