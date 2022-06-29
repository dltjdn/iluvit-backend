package FIS.iLUVit.repository;

import FIS.iLUVit.domain.alarms.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select alarm from Alarm alarm where alarm.user.id =:userId")
    List<Alarm> findByUser(@Param("userId") Long userId);
}
