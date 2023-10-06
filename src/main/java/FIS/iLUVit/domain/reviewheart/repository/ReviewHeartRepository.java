package FIS.iLUVit.domain.reviewheart.repository;

import FIS.iLUVit.domain.review.domain.Review;
import FIS.iLUVit.domain.reviewheart.domain.ReviewHeart;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {

    /**
     * 해당 리뷰로 리뷰 좋아요를 조회합니다
     */
    List<ReviewHeart> findByReview(Review review);

    /**
     * 해당 리뷰와 사용자로 리뷰 좋아요를 조회합니다
     */
    Optional<ReviewHeart> findByReviewAndUser(Review review, User user);
}
