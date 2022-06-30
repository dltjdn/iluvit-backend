package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class CenterApprovalReceivedAlarm extends Alarm{

    public CenterApprovalReceivedAlarm(User teacher) {
        super(teacher);
        this.mode = AlarmUtils.CENTER_APPROVAL_RECEIVED;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new CenterApprovalReceivedAlarmDto(message, dtype);
    }

    @Getter
    public static class CenterApprovalReceivedAlarmDto extends AlarmDto{
        public CenterApprovalReceivedAlarmDto(String message, String type) {
            super(message, type);
        }
    }
}
