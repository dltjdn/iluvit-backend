package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 설명회 알림 발생 시나리오
 * 1. 찜한 시설에서 설명회가 생성 되었을 때 - 학부모
 * 2. 대기 신청한 설명회에서 신청으로 상태가 변경 되었을 때 - 학부모
 * 3. 원장 - 설명회의 인원이 다 차게 되었을 때 발생
 * 4. 원장 - 설명회 신청기간 마감 되었을 때
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PresentationCreatedAlarm extends Alarm {

    // 누구를 대상으로 한 알람?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentationId")
    private Presentation presentation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerId")
    private Center center;

    public PresentationCreatedAlarm(User user, Presentation presentation, Center center) {
        super(user);
        this.mode = AlarmUtils.PRESENTATION_CREATED_LIKED_CENTER;
        this.center = center;
        this.presentation = presentation;
        String args[] = {center.getName()};
        message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PresentationCreatedAlarmDto(message, dtype, center.getId(), presentation.getId());
    }

    @Getter
    public static class PresentationCreatedAlarmDto extends AlarmDto{
        protected Long presentationId;
        protected Long centerId;

        public PresentationCreatedAlarmDto(String message, String type, Long centerId, Long presentationId) {
            super(message, type);
            this.centerId = centerId;
            this.presentationId = presentationId;
        }
    }
}
