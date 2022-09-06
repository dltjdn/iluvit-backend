package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByTargetId(Long targetId);
}
