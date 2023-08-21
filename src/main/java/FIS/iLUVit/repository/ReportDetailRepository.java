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
        userId와 targetId에 해당하는 ReportDetailPost 객체를 검색하여 ReportDetail을 불러옵니다.
     */
    @Query("select rdp " +
            "from ReportDetailPost rdp " +
            "where rdp.user.id = :userId " +
            "and rdp.post.id = :targetId ")
    Optional<ReportDetail> findByUserIdAndTargetPostId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /*
        userId와 targetId에 해당하는 ReportDetailComment 객체를 검색하여 ReportDetail을 불러옵니다.
     */
    @Query("select rdc " +
            "from ReportDetailComment rdc " +
            "where rdc.user.id = :userId " +
            "and rdc.comment.id = :targetId ")
    Optional<ReportDetail> findByUserIdAndTargetCommentId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /*
        postId에 해당하는 게시물 ID를 가진 ReportDetailPost 객체들의 post 속성을 null로 업데이트합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ReportDetailPost  rdp set rdp.post = null where rdp.post.id = :postId")
    void setPostIsNull(@Param("postId") Long postId);

    /*
        commentIds에 해당하는 댓글 ID를 가진 ReportDetailComment 객체들의 comment 속성을 null로 업데이트합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ReportDetailComment  rdc set rdc.comment = null where rdc.comment.id in :commentIds")
    void setCommentIsNull(@Param("commentIds") List<Long> commentIds);

    /**
     * 해당 Report Id로 ReportDetail을 조회합니다
     */
    ReportDetail findByReportId(Long reportId);
}
