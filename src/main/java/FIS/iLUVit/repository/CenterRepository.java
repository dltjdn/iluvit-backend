package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import FIS.iLUVit.repository.dto.CenterInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select new FIS.iLUVit.repository.dto.CenterInfoDto(center) From Center center where center.id =:id")
    public CenterInfoDto findInfoById(@Param("id") Long id);

    @Query("select new FIS.iLUVit.repository.dto.CenterBannerDto(center.id, center.name, center.maxChildCnt, center.curChildCnt, center.signed, center.recruit, center.waitingNum) " +
            "from Center center where center.id=:id")
    public CenterBannerDto findBannerById(@Param("id") Long id);
}