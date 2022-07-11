package FIS.iLUVit.repository;

import FIS.iLUVit.domain.alarms.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select alarm from Alarm alarm where alarm.user.id =:userId")
    List<Alarm> findByUser(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Alarm alarm where alarm.id= :alarmId and alarm.user.id = :userId")
    Integer deleteById(@Param("userId") Long userId, @Param("alarmId") Long alarmId);

    @Modifying(clearAutomatically = true)
    @Query("update PostAlarm pa set pa.post.id = null where pa.post.id = :postId")
    Integer setPostIsNull(@Param("postId") Long postId);
}
