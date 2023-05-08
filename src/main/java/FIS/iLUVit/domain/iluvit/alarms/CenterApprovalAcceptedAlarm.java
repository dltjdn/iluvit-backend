package FIS.iLUVit.domain.iluvit.alarms;

import FIS.iLUVit.dto.alarm.AlarmDetailDto;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.service.AlarmUtils;
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
    public AlarmDetailDto exportAlarm() {
        return new CenterApprovalAcceptedAlarmDto(id, createdDate, message, dtype, centerId, centerName);
    }

    @Getter
    public static class CenterApprovalAcceptedAlarmDto extends AlarmDetailDto {
        protected Long centerId;
        protected String centerName;

        public CenterApprovalAcceptedAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long centerId, String centerName) {
            super(id, createdDate, message, type);
            this.centerId = centerId;
            this.centerName = centerName;
        }
    }
}