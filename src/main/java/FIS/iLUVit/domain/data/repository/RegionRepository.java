package FIS.iLUVit.domain.data.repository;

import FIS.iLUVit.domain.data.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     * 해당 시도명으로 Region 리스트를 조회합니다
     */
    List<Region> findRegionBySidoName(String sidoName);

    /**
     * 해당 시군구명으로 Region을 조회합니다
     */
    Region findBySigunguName(String sigunguName);

}
