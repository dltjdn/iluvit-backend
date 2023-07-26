package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * targetId에 해당하는 신고내역 조회합니다
     */
    Optional<Report> findByTargetId(Long targetId);

    /**
     * target Id에 해당하는 신고내역의 targetId를 null로 하고 상태를 DELETE로 업데이트한다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Report r " +
            "SET r.targetId = null, r.status = 'DELETE' "+
            "WHERE r.targetId = :targetId ")
    void setTargetIsNullAndStatusIsDelete(Long targetId);

    /**
     * target Id 리스트에 해당하는 신고내역의 targetId를 null로 하고 상태를 DELETE로 업데이트한다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Report r " +
            "SET r.targetId = null, r.status = 'DELETE' "+
            "WHERE r.targetId in :targetIds")
    void setTargetIsNullAndStatusIsDelete(List<Long> targetIds);
}