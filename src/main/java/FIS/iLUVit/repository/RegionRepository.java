package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findRegionBySidoName(String sidoName);

    Region findBySigunguName(String sigunguName);
}
