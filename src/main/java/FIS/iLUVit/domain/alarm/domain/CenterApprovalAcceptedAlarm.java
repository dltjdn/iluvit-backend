package FIS.iLUVit.domain.alarm.domain;

import FIS.iLUVit.domain.alarm.dto.AlarmResponse;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.alarm.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CenterApprovalAcceptedAlarm extends Alarm{

    private Long centerId;
    private String centerName;

    public CenterApprovalAcceptedAlarm(User user, Center center) {
        super(user);
        this.mode = AlarmUtils.CENTER_APPROVAL_ACCEPTED;
        this.centerId = center.getId();
        this.centerName = center.getName();
        String args[] = {center.getName()};
        message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmResponse exportAlarm() {
        return new CenterApprovalAcceptedAlarmResponse(id, createdDate, message, dtype, centerId, centerName);
    }

    @Getter
    public static class CenterApprovalAcceptedAlarmResponse extends AlarmResponse {
        protected Long centerId;
        protected String centerName;

        public CenterApprovalAcceptedAlarmResponse(Long id, LocalDateTime createdDate, String message, String type, Long centerId, String centerName) {
            super(id, createdDate, message, type);
            this.centerId = centerId;
            this.centerName = centerName;
        }
    }
}