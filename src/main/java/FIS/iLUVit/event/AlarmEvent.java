package FIS.iLUVit.event;

import FIS.iLUVit.domain.iluvit.alarms.Alarm;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AlarmEvent {

    private Alarm alarm;

    public AlarmEvent(Alarm alarm) {
        this.alarm = alarm;
    }
}
