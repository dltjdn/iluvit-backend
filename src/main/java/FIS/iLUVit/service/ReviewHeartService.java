package FIS.iLUVit.service;

import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.ReviewHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ReviewException;
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
        reviewHeartRepository.findByReviewAndUser(reviewId, userId)
                .ifPresent(m -> {
                    throw new ReviewException("이미 좋아요한 리뷰에 좋아요 불가능");
                });
        Review findReview = reviewRepository.getById(reviewId);
        User user = userRepository.getById(userId);
        ReviewHeart reviewHeart = new ReviewHeart(findReview, user);
        reviewHeartRepository.save(reviewHeart);
    }

    public void deleteReviewHeart(Long reviewId, Long userId) {
        // 리뷰 좋아요한 데이터가 존재하면 삭제, 존재하지 않는데 삭제 요청을 보내면 Exception 터뜨림
        reviewHeartRepository.findByReviewAndUser(reviewId, userId)
                .ifPresentOrElse(reviewHeartRepository::delete, () -> {
                    throw new ReviewException("존재하지 않는 좋아요 취소 시도");
                });
    }
}
