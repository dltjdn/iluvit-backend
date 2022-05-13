package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AddInfo;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select distinct center " +
            "From Center center " +
            "join fetch center.programs " +
            "where center.id =:id")
    Optional<Center> findInfoByIdWithProgram(@Param("id") Long id);

    @Query("select center.addInfos from Center center where center.id =:id")
    List<AddInfo> findInfoByIdWithAddInfo(@Param("id") Long id);


    @Query("select new FIS.iLUVit.repository.dto.CenterBannerDto(center.id, center.name, center.maxChildCnt, center.curChildCnt, center.signed, center.recruit, center.waitingNum) " +
            "from Center center where center.id=:id")
    CenterBannerDto findBannerById(@Param("id") Long id);
}