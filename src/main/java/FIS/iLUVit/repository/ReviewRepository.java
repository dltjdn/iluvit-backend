package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByParent(Parent parent); // 학부모가 작성한 리뷰 찾기

    @Query("select r from Review r join r.center c where c.id = :centerId ")
    List<Review> findByCenter(@Param("centerId") Long centerId);
}
