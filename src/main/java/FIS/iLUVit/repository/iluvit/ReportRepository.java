package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByTargetId(Long targetId);

    @Modifying(clearAutomatically = true)
    @Query("update Report r " +
            "set r.targetId = null, r.status = 'DELETE' "+
            "where r.targetId =:postId ")
    void setTargetIsNullAndStatusIsDelete(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update Report r " +
            "set r.targetId = null, r.status = 'DELETE' "+
            "where r.targetId in :commentIds")
    void setTargetIsNullAndStatusIsDelete(@Param("commentIds") List<Long> commentIds);
}
