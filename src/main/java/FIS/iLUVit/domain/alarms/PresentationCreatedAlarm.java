package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.MessageUtils;
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
        this.mode = MessageUtils.PRESENTATION_CREATED_LIKED_CENTER;
        this.center = center;
        this.presentation = presentation;
        String args[] = {center.getName()};
        message = MessageUtils.getMessage(mode, args);
    }
}
