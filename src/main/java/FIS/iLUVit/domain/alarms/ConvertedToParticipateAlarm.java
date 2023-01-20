package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDetailDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ConvertedToParticipateAlarm extends Alarm {

    @Column(name = "presentationId")
    private Long presentationId;

    @Column(name = "centerId")
    private Long centerId;

    public ConvertedToParticipateAlarm(User waiter, Presentation presentation, Center center) {
        super(waiter);
        String[] args = {center.getName()};
        this.presentationId = presentation.getId();
        this.centerId = center.getId();
        this.mode = AlarmUtils.PRESENTATION_WAITING_TO_PARTICIPATE;
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDetailDto exportAlarm() {
        return new PresentationConvertedToParticipateAlarmDto(id, createdDate, message, dtype, centerId, presentationId);
    }

    @Getter
    public static class PresentationConvertedToParticipateAlarmDto extends AlarmDetailDto {
        protected Long presentationId;
        protected Long centerId;

        public PresentationConvertedToParticipateAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long centerId, Long presentationId) {
            super(id, createdDate, message, type);
            this.centerId = centerId;
            this.presentationId = presentationId;
        }
    }
}
