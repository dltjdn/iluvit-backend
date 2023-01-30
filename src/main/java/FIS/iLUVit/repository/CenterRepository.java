package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.dto.center.CenterBannerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select new FIS.iLUVit.dto.center.CenterBannerDto(center.id, center.name, center.signed, center.recruit, avg(review.score), prefer.center.id, center.profileImagePath, center.infoImagePath) " +
            "from Center center " +
            "left join center.reviews as review " +
            "left join center.prefers as prefer on prefer.parent.id = :userId " +
            "where center.id=:id " +
            "group by center")
    CenterBannerDto findBannerById(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select new FIS.iLUVit.dto.center.CenterBannerDto(center.id, center.name, center.signed, center.recruit, avg(review.score), center.profileImagePath, center.infoImagePath) " +
            "from Center center " +
            "left join center.reviews as review " +
            "where center.id=:id " +
            "group by center")
    CenterBannerDto findBannerById(@Param("id") Long id);

    @Query("select c from Center c join c.teachers t where t.id = :teacherId")
    Optional<Center> findCenterByTeacher(@Param("teacherId") Long teacherId);

    @Query("select ct from Child c join c.center ct join c.parent p where p.id = :userId")
    List<Center> findByUser(@Param("userId") Long userId);

    @Query("select distinct c " +
            "from Center c " +
            "left join fetch c.teachers " +
            "where c.id =:centerId")
    Optional<Center> findByIdWithTeacher(@Param("centerId") Long centerId);

    @Query("select distinct c " +
            "from Center c " +
            "join fetch c.teachers t " +
            "where c.id =:centerId " +
            "and t.approval = 'ACCEPT' " +
            "and c.signed = true")
    Optional<Center> findByIdAndSignedWithTeacher(@Param("centerId") Long center_id);

}