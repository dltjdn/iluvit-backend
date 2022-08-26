package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select pr " +
            "from PostReport pr " +
            "where pr.post.id = :targetPostId " +
            "and pr.user.id = :userId ")
    Optional<Report> findByTargetPostIdAndUserId(@Param("targetPostId") Long targetPostId, @Param("userId") Long userId);

    @Query("select cr " +
            "from CommentReport cr " +
            "where cr.comment.id = :targetCommentId " +
            "and cr.user.id = :userId ")
    Optional<Report> findByTargetCommentIdAndUserId(@Param("targetCommentId") Long targetCommentId, @Param("userId") Long userId);
}
