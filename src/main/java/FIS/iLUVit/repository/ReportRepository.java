package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /*
        대상 id로 신고를 조회합니다.
     */
    Optional<Report> findByTargetId(Long targetId);

    /*
        게시글 id를 파라미터로 받아서 대상이 Null이고 상태가 삭제가 되도록 설정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Report r " +
            "set r.targetId = null, r.status = 'DELETE' "+
            "where r.targetId =:postId ")
    void setTargetIsNullAndStatusIsDelete(@Param("postId") Long postId);

    /*
        댓글 id들을 파라미터로 받아서 대상이 Null이고 상태가 삭제가 되도록 설정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Report r " +
            "set r.targetId = null, r.status = 'DELETE' "+
            "where r.targetId in :commentIds")
    void setTargetIsNullAndStatusIsDelete(@Param("commentIds") List<Long> commentIds);
}
