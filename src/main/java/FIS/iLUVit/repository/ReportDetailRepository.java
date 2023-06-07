package FIS.iLUVit.repository;

import FIS.iLUVit.domain.reports.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long> {

    /*
        사용자 Id와 대상 Id를 파라미터로 받아서 사용자 ID 및 대상 게시글 ID로 조회합니다.
     */
    @Query("select rdp " +
            "from ReportDetailPost rdp " +
            "where rdp.user.id =:userId " +
            "and rdp.post.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetPostId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /*
        사용자 Id와 타겟 Id를 파라미터로 받아서 사용자 id 및 대상 댓글 id로 조회합니다.
     */
    @Query("select rdc " +
            "from ReportDetailComment rdc " +
            "where rdc.user.id =:userId " +
            "and rdc.comment.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetCommentId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /*
        게시글 id를 파라미터로 받아서 게시글을 존재하지 않게 설정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ReportDetailPost  rdp set rdp.post = null where rdp.post.id =:postId")
    void setPostIsNull(@Param("postId") Long postId);

    /*
        댓글 id들을 파라미터로 받아서 댓글을 존재하지 않게 설정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ReportDetailComment  rdc set rdc.comment = null where rdc.comment.id in :commentIds")
    void setCommentIsNull(@Param("commentIds") List<Long> commentIds);
}
