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
     * 해당 신고할 대상(게시글, 댓글) id로 신고를 조회합니다
     */
    Optional<Report> findByTargetId(Long targetId);

    /**
     * 해당 post id를 가진 신고의 target id를 null로, status를 DELETE로 변경합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Report r " +
            "SET r.targetId = null, r.status = 'DELETE' "+
            "WHERE r.targetId =:postId ")
    void setTargetIsNullAndStatusIsDelete(@Param("postId") Long postId);

    /**
     * 해당 comment id를 가진 신고의 target id를 null로, status를 DELETE로 변경합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Report r " +
            "SET r.targetId = null, r.status = 'DELETE' "+
            "WHERE r.targetId in :commentIds")
    void setTargetIsNullAndStatusIsDelete(@Param("commentIds") List<Long> commentIds);
}