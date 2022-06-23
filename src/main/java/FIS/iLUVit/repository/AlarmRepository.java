package FIS.iLUVit.repository;

import FIS.iLUVit.domain.alarms.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
