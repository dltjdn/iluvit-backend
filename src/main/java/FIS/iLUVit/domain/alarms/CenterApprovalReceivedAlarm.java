package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
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
}
