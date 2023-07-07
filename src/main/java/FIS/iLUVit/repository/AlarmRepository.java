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

    /**
        작성날짜: 2023/07/07 6:38 PM
        작성자: 이서우
        작성내용: 알람 type이 "설명회 가득참", "설명회 참여로 전환" ,"설명회 생성" , "설명회 기간 종료" 알람이 아닌 것들중에서 사용자별로 조회합니다
    */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "alarm.dtype <> 'PresentationFullAlarm' and alarm.dtype <> 'ConvertedToParticipateAlarm' and alarm.dtype <> 'PresentationCreatedAlarm' and alarm.dtype <> 'PresentationPeriodClosedAlarm' ")
    Slice<Alarm> findActiveByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     작성날짜: 2023/07/07 6:38 PM
     작성자: 이서우
     작성내용: 알람 type이 "설명회 가득참", "설명회 참여로 전환" ,"설명회 생성" , "설명회 기간 종료" 인 알람 중에서 사용자별로 조회합니다
     */
    @Query("select alarm from Alarm alarm " +
            "where alarm.user.id =:userId and " +
            "alarm.dtype = 'PresentationFullAlarm' or alarm.dtype = 'ConvertedToParticipateAlarm' or alarm.dtype = 'PresentationCreatedAlarm' or alarm.dtype = 'PresentationPeriodClosedAlarm'")
    Slice<Alarm> findPresentationByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        게시글 알람 id가 게시글 id인 것들의 게시글 id를 null로 업데이트합니다.
    */
    @Modifying(clearAutomatically = true)
    @Query("update PostAlarm pa set pa.postId = null where pa.postId = :postId")
    Integer setPostIsNull(@Param("postId") Long postId);


    /*
        알람 id와 알람사용자 id별로 알람을 삭제합니다.
    */
//    @Modifying
//    @Query("delete from Alarm alarm where alarm.id in :alarmIds and alarm.user.id = :userId")
//    Integer deleteByIds(@Param("userId") Long userId, @Param("alarmIds") List<Long> alarmIds);
    void deleteByUserIdAndIdIn(Long userId, List<Long> alarmIds); //


    /**
        작성날짜: 2023/07/07 6:47 PM
        작성자: 이서우
        작성내용: 해당 사용자의 모든 알람을 삭제합니다.
    */
    void deleteAllByUser(User user);
}
