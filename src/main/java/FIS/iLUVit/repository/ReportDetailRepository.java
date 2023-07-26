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
            "where rdp.user.id =:userId " +
            "and rdp.post.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetPostId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /*
        userId와 targetId에 해당하는 ReportDetailComment 객체를 검색하여 ReportDetail을 불러옵니다.
     */
    @Query("select rdc " +
            "from ReportDetailComment rdc " +
            "where rdc.user.id =:userId " +
            "and rdc.comment.id =:targetId ")
    Optional<ReportDetail> findByUserIdAndTargetCommentId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    /**
     * post Id에 해당하는 신고상세내역의 post를 null로 업데이트한다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReportDetailPost rdp SET rdp.post = null WHERE rdp.post.id =:postId")
    void setPostIsNull(Long postId);

    /**
     * commnetId 리스트에 해당하는 신고상세내역의 comment를 null로 업데이트 한다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReportDetailComment rdc SET rdc.comment = null WHERE rdc.comment.id in :commentIds")
    void setCommentIsNull(List<Long> commentIds);
}
