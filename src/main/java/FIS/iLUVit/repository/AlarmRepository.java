package FIS.iLUVit.repository;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    /**
     * 알림종류가 "설명회 가득참", "설명회 참여로 전환" ,"설명회 생성" , "설명회 기간 종료" 가 아닌 해당 사용자의 알림을 조회합니다
     */
    @Query("SELECT alarm FROM Alarm alarm " +
            "WHERE alarm.user.id = :userId AND " +
            "(alarm.dtype <> 'PresentationFullAlarm' AND alarm.dtype <> 'ConvertedToParticipateAlarm' AND alarm.dtype <> 'PresentationCreatedAlarm' AND alarm.dtype <> 'PresentationPeriodClosedAlarm')")
    Slice<Alarm> findActiveByUser(Long userId, Pageable pageable);

    /**
     * 알림종류가 "설명회 가득참", "설명회 참여로 전환" ,"설명회 생성" , "설명회 기간 종료" 인 해당 사용자의 알림을 조회합니다
     */
    @Query("SELECT alarm FROM Alarm alarm " +
            "WHERE alarm.user.id = :userId AND " +
            "(alarm.dtype = 'PresentationFullAlarm' OR alarm.dtype = 'ConvertedToParticipateAlarm' OR alarm.dtype = 'PresentationCreatedAlarm' OR alarm.dtype = 'PresentationPeriodClosedAlarm')")
    Slice<Alarm> findPresentationByUser(Long userId, Pageable pageable);

    /**
     * 해당 사용자의 알람 id 리스트를 삭제합니다
     */
    void deleteByUserIdAndIdIn(Long userId, List<Long> alarmIds);

    /**
     * 해당 사용자의 모든 알람을 삭제합니다.
     */
    void deleteAllByUser(User user);

    /**
     * 해당 게시글 id를 가진 알림들의 게시글 id를 null로 업데이트합니다.
    */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostAlarm pa SET pa.postId = null WHERE pa.postId = :postId")
    Integer setPostIsNull( Long postId );

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
