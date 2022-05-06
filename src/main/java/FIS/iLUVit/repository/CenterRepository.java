package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

}