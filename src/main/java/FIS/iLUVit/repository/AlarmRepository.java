package FIS.iLUVit.repository;

import FIS.iLUVit.event.AlarmEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<AlarmEvent, Long> {

}
