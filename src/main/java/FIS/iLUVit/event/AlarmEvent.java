package FIS.iLUVit.event;

import FIS.iLUVit.domain.alarms.Alarm;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AlarmEvent {

    private Alarm alarm;

    public AlarmEvent(Alarm alarm) {
        this.alarm = alarm;
    }

    public static AlarmEvent createAlarmEvent(Alarm alarm){
        return new AlarmEvent(alarm);
    }
}
