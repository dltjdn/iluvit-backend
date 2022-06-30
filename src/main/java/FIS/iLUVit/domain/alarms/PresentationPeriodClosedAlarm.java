package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@Getter
public class PresentationPeriodClosedAlarm extends Alarm{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentationId")
    private Presentation presentation;

    public PresentationPeriodClosedAlarm(User user, Presentation presentation) {
        super(user);
        this.mode = AlarmUtils.PRESENTATION_CLOSED;
        this.presentation = presentation;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PresentationPeriodClosedAlarmDto(message, dtype, presentation.getId());
    }

    @Getter
    public static class PresentationPeriodClosedAlarmDto extends AlarmDto{

        protected Long presentationId;

        public PresentationPeriodClosedAlarmDto(String message, String type, Long presentationId) {
            super(message, type);
            this.presentationId = presentationId;
        }
    }

}
