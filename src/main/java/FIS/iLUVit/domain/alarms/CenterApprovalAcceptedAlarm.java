package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CenterApprovalAcceptedAlarm extends Alarm{

    @Column(name = "centerId")
    private Long centerId;

    public CenterApprovalAcceptedAlarm(User user, Center center) {
        super(user);
        this.mode = AlarmUtils.CENTER_APPROVAL_ACCEPTED;
        this.centerId = center.getId();
        String args[] = {center.getName()};
        message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new CenterApprovalAcceptedAlarmDto(id, createdDate, message, dtype, centerId);
    }

    @Getter
    public static class CenterApprovalAcceptedAlarmDto extends AlarmDto{
        protected Long centerId;

        public CenterApprovalAcceptedAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long centerId) {
            super(id, createdDate, message, type);
            this.centerId = centerId;
        }
    }
}
