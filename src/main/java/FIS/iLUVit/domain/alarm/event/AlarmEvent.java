package FIS.iLUVit.domain.alarm.event;

import FIS.iLUVit.domain.alarm.domain.Alarm;
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
