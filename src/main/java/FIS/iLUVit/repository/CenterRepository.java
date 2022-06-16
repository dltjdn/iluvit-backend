package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AddInfo;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select new FIS.iLUVit.repository.dto.CenterBannerDto(center.id, center.name, center.maxChildCnt, center.curChildCnt, center.signed, center.recruit, center.waitingNum, avg(review.score)) " +
            "from Center center " +
            "left join center.reviews as review " +
            "where center.id=:id " +
            "group by center")
    CenterBannerDto findBannerById(@Param("id") Long id);

    @Query("select center.id from Center center " +
            "where center.theme =:theme and :interestAge between center.minAge and center.maxAge")
    List<Long> findByThemeAndAgeOnly3(@Param("theme") Theme theme, @Param("interestAge") Integer interestAge, Pageable pageable);

    @Query("select c from Center c join c.teachers t where t.id = :teacherId")
    Optional<Center> findCenterByTeacher(@Param("teacherId") Long teacherId);
}