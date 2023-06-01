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

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    /*
        유저 id를 파라미터로 정하여 유저 id에 맞는 활동을 찾음.
     */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "alarm.dtype <> 'PresentationFullAlarm' and alarm.dtype <> 'ConvertedToParticipateAlarm' and alarm.dtype <> 'PresentationCreatedAlarm' and alarm.dtype <> 'PresentationPeriodClosedAlarm' ")
    Slice<Alarm> findActiveByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        유저 id를 파라미터로 정하여 유저 id에 맞는 설명회를 찾음.
     */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "(alarm.dtype = 'PresentationFullAlarm' or alarm.dtype = 'ConvertedToParticipateAlarm' or alarm.dtype = 'PresentationCreatedAlarm' or alarm.dtype = 'PresentationPeriodClosedAlarm')")
    Slice<Alarm> findPresentationByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        유저 id와 알람 id 여러 개를 파라미터 값으로 받아 id들을 삭제한다.
     */
    @Modifying
    @Query("delete from Alarm alarm where alarm.id in :alarmIds and alarm.user.id = :userId")
    Integer deleteByIds(@Param("userId") Long userId, @Param("alarmIds") List<Long> alarmIds);

    /*
        게시글 id를 파라미터로 받아서 게시글은 null이 아니다를 세팅함.
     */
    @Modifying(clearAutomatically = true)
    @Query("update PostAlarm pa set pa.postId = null where pa.postId = :postId")
    Integer setPostIsNull(@Param("postId") Long postId);

    /*
        User 도메인에 있는 user 정보들을 모두 제거함.
    */
    void deleteAllByUser(User user);
}
