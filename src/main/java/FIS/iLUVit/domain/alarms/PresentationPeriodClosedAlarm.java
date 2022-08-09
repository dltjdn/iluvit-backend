package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PresentationPeriodClosedAlarm extends Alarm{

    @Column(name = "presentationId")
    private Long presentationId;

    @Column(name = "centerId")
    private Long centerId;

    public PresentationPeriodClosedAlarm(User user, Presentation presentation, Center center) {
        super(user);
        this.mode = AlarmUtils.PRESENTATION_CLOSED;
        this.presentationId = presentation.getId();
        this.centerId = center.getId();
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PresentationPeriodClosedAlarmDto(id, createdDate, message, dtype, presentationId, centerId);
    }

    @Getter
    public static class PresentationPeriodClosedAlarmDto extends AlarmDto{

        protected Long presentationId;
        protected Long centerId;

        public PresentationPeriodClosedAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long presentationId, Long centerId) {
            super(id, createdDate, message, type);
            this.presentationId = presentationId;
            this.centerId = centerId;
        }
    }

}
