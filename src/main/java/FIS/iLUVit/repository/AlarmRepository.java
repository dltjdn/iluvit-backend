package FIS.iLUVit.repository;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    /*
        알람 dtype이 설명회 전체 알람이 아니고 참여 알람으로 전환되지 않고 설명회 생성 알람이 아니고 설명회 기간 종료 알람이 아닌 것들중에서 사용자별로 조회합니다.
    */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "alarm.dtype <> 'PresentationFullAlarm' and alarm.dtype <> 'ConvertedToParticipateAlarm' and alarm.dtype <> 'PresentationCreatedAlarm' and alarm.dtype <> 'PresentationPeriodClosedAlarm' ")
    Slice<Alarm> findActiveByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        사용자 id별로 설명회 알람을 조회합니다.
    */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "(alarm.dtype = 'PresentationFullAlarm' or alarm.dtype = 'ConvertedToParticipateAlarm' or alarm.dtype = 'PresentationCreatedAlarm' or alarm.dtype = 'PresentationPeriodClosedAlarm')")
    Slice<Alarm> findPresentationByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        알람 id와 알람사용자 id별로 알람을 삭제합니다.
    */
    @Modifying
    @Query("delete from Alarm alarm where alarm.id in :alarmIds and alarm.user.id = :userId")
    Integer deleteByIds(@Param("userId") Long userId, @Param("alarmIds") List<Long> alarmIds);

    /*
        게시글 알람 id가 게시글 id인 것들의 게시글 id를 null로 업데이트합니다.
    */
    @Modifying(clearAutomatically = true)
    @Query("update PostAlarm pa set pa.postId = null where pa.postId = :postId")
    Integer setPostIsNull(@Param("postId") Long postId);

    /*
        사용자별로 알람을 삭제합니다.
    */
    void deleteAllByUser(User user);

    /**
     * 해당 유저와 게시글 기본키로 댓글 알림을 삭제합니다
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PostAlarm a WHERE a.commentUserNickname = :commentUserNickname AND a.postId = :postId")
    void deleteByBlockedUserAndPostId(String commentUserNickname, Long postId);

    /**
     * 해당 유저와 발신자 기본키로 채팅 알림을 삭제합니다
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatAlarm a WHERE a.user = :user AND a.senderId = :senderId")
    void deleteByUserAndSenderId(User user, Long senderId);
}
