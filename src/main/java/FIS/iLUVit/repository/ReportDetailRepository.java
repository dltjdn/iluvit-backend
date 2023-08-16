package FIS.iLUVit.repository;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.reports.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long> {

    /**
     * 해당 사용자와 신고할 대상(게시글, 댓글) id로 ReportDetail을 조회합니다
     */
    Optional<ReportDetail> findByUserAndReportTargetId(User user, Long targetId);

    /**
     * postId에 해당하는 게시물 ID를 가진 ReportDetailPost 객체들의 post 속성을 null로 업데이트합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReportDetailPost  rdp SET rdp.post = null WHERE rdp.post.id =:postId")
    void setPostIsNull(@Param("postId") Long postId);

    /**
     * commentIds에 해당하는 댓글 ID를 가진 ReportDetailComment 객체들의 comment 속성을 null로 업데이트합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReportDetailComment  rdc SET rdc.comment = null WHERE rdc.comment.id in :commentIds")
    void setCommentIsNull(@Param("commentIds") List<Long> commentIds);

}
