package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PresentationFullAlarm extends Alarm{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentationId")
    private Presentation presentation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerId")
    private Center center;

    public PresentationFullAlarm(User user, Presentation presentation, Center center) {
        super(user);
        this.mode = AlarmUtils.PRESENTATION_APPLICANTS_FULL;
        this.presentation = presentation;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PresentationFullAlarmDto(id, createdDate, message, dtype, presentation.getId());
    }

    @Getter
    public static class PresentationFullAlarmDto extends AlarmDto{

        protected Long presentationId;

        public PresentationFullAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long presentationId) {
            super(id, createdDate, message, type);
            this.presentationId = presentationId;
        }
    }
}
