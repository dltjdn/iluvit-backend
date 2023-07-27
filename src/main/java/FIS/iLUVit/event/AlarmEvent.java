package FIS.iLUVit.event;

import FIS.iLUVit.domain.alarms.Alarm;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AlarmEvent {

    private Alarm alarm;
    private String type;

    public AlarmEvent(Alarm alarm, String type) {
        this.alarm = alarm;
        this.type = type;
    }
}
