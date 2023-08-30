package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.dto.alarm.AlarmResponse;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.Center;
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
    private String centerName;
    private Long centerId;

    public CenterApprovalReceivedAlarm(User teacher, Auth auth, Center center) {
        super(teacher);
        this.auth = auth;
        this.centerName = center.getName();
        this.centerId = center.getId();
        this.mode = AlarmUtils.CENTER_APPROVAL_RECEIVED;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmResponse exportAlarm() {
        return new CenterApprovalReceivedAlarmResponse(id, createdDate, message, dtype, auth, centerName,centerId);
    }

    @Getter
    public static class CenterApprovalReceivedAlarmResponse extends AlarmResponse {

        private Auth auth;
        private String centerName;
        private Long centerId;

        public CenterApprovalReceivedAlarmResponse(Long id, LocalDateTime createdDate, String message, String type, Auth auth, String centerName, Long centerId) {
            super(id, createdDate, message, type);
            this.auth = auth;
            this.centerName = centerName;
            this.centerId = centerId;
        }
    }
}
