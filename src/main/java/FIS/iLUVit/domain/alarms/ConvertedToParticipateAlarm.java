package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.dto.alarm.AlarmResponse;
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

    @Column(name = "centerName")
    private String centerName;

    public ConvertedToParticipateAlarm(User waiter, Presentation presentation, Center center) {
        super(waiter);
        String[] args = {center.getName()};
        this.presentationId = presentation.getId();
        this.centerId = center.getId();
        this.centerName = center.getName();
        this.mode = AlarmUtils.PRESENTATION_WAITING_TO_PARTICIPATE;
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmResponse exportAlarm() {
        return new PresentationConvertedToParticipateAlarmResponse(id, createdDate, message, dtype, centerId, presentationId, centerName);
    }

    @Getter
    public static class PresentationConvertedToParticipateAlarmResponse extends AlarmResponse {
        protected Long presentationId;
        protected Long centerId;
        protected String centerName;

        public PresentationConvertedToParticipateAlarmResponse(Long id, LocalDateTime createdDate, String message, String type, Long centerId, Long presentationId, String centerName) {
            super(id, createdDate, message, type);
            this.centerId = centerId;
            this.centerName = centerName;
            this.presentationId = presentationId;
        }
    }
}
