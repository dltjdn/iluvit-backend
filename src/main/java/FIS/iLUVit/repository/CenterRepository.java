package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.repository.dto.CenterInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select new FIS.iLUVit.repository.dto.CenterInfoDto(center) From Center center where center.id =:id")
    public CenterInfoDto findInfoById(@Param("id") Long id);
}