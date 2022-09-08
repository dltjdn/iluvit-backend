package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long> {

    @Query("select rdp " +
            "from ReportDetailPost rdp " +
            "where rdp.user.id =:userId " +
            "and rdp.post.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetPostId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select rdc " +
            "from ReportDetailComment rdc " +
            "where rdc.user.id =:userId " +
            "and rdc.comment.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetCommentId(@Param("userId") Long userId, @Param("targetId") Long targetId);
}
