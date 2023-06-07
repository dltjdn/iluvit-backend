package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>{

    /*
        부모 id를 파라미터로 받아서 부모로 리뷰를 조회합니다.
     */
    @Query("select r from Review r join fetch r.center c join fetch r.parent p where p.id = :parentId")
    Slice<Review> findByParent(@Param("parentId") Long parentId, Pageable pageable); // 학부모가 작성한 리뷰 찾기

    /*
        시설 id를 파라미터로 받아서 시설 id로 리뷰를 조회합니다.
     */
    @Query(value = "select r from Review r join fetch r.center c join fetch r.parent p where c.id = :centerId",
            countQuery = "select count(r) from Review r where r.center.id = :centerId order by r.createdDate desc ")
    Slice<Review> findByCenterId(@Param("centerId") Long centerId, Pageable pageable);

    /*
        사용자 id와 시설 id를 파라미터로 받아 사용자와 시설로 리뷰를 조회합니다.
     */
    @Query("select r from Review r join r.center c join r.parent p where c.id = :centerId and p.id = :userId")
    Optional<Review> findByUserAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        시설로 리뷰를 조회합니다.
     */
    List<Review> findByCenter(Center center);
}
