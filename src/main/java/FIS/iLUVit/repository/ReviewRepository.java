package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r join fetch r.center c join fetch r.parent p where p.id = :parentId")
    List<Review> findByParent(@Param("parentId") Long parentId); // 학부모가 작성한 리뷰 찾기

    @Query("select r from Review r join fetch r.center c join fetch r.parent p where c.id = :centerId")
    List<Review> findByCenterAndParent(@Param("centerId") Long centerId);

    @Query("select r from Review r join r.center c join r.parent p where c.id = :centerId and p.id = :userId")
    Optional<Review> findByUserAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);
}
