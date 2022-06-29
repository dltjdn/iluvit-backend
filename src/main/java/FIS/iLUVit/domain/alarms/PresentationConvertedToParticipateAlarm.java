package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class PresentationConvertedToParticipateAlarm extends Alarm {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentationId")
    private Presentation presentation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerId")
    private Center center;

    public PresentationConvertedToParticipateAlarm(User waiter, Presentation presentation, Center center) {
        super(waiter);
        String[] args = {center.getName()};
        this.presentation = presentation;
        this.center = center;
        this.mode = AlarmUtils.PRESENTATION_WAITING_TO_PARTICIPATE;
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PresentationConvertedToParticipateAlarmDto(message, dtype, center.getId(), presentation.getId());
    }

    @Getter
    public static class PresentationConvertedToParticipateAlarmDto extends AlarmDto{
        protected Long presentationId;
        protected Long centerId;

        public PresentationConvertedToParticipateAlarmDto(String message, String type, Long centerId, Long presentationId) {
            super(message, type);
            this.centerId = centerId;
            this.presentationId = presentationId;
        }
    }
}
