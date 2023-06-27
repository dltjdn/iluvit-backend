package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ReviewHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {

    /*
        reviewId와 userId에 해당하는 리뷰 ID와 사용자 ID로 ReviewHeart를 조회합니다.
     */
    @Query("select rh from ReviewHeart rh where rh.review.id = :reviewId and rh.user.id = :userId")
    Optional<ReviewHeart> findByReviewAndUser(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
