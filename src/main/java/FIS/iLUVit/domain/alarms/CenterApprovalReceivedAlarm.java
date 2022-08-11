package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CenterApprovalReceivedAlarm extends Alarm{

    @Enumerated(EnumType.STRING)
    private Auth auth;

    public CenterApprovalReceivedAlarm(User teacher, Auth auth) {
        super(teacher);
        this.auth = auth;
        this.mode = AlarmUtils.CENTER_APPROVAL_RECEIVED;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new CenterApprovalReceivedAlarmDto(id, createdDate, message, dtype, auth);
    }

    @Getter
    public static class CenterApprovalReceivedAlarmDto extends AlarmDto{

        private Auth auth;

        public CenterApprovalReceivedAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Auth auth) {
            super(id, createdDate, message, type);
            this.auth = auth;
        }
    }
}
