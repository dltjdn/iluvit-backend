package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ReviewHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {

    /*
        리뷰 id와 사용자 id를 파라미터로 받아서 리뷰와 사용자로 리뷰하트를 조회합니다.
     */
    @Query("select rh from ReviewHeart rh where rh.review.id = :reviewId and rh.user.id = :userId")
    Optional<ReviewHeart> findByReviewAndUser(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
