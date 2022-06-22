package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.Mode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

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
public class PresentationAlarm extends Alarm {

    // 누구를 대상으로 한 알람?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Presentation presentation;

    @Override
    public Alarm createMessage(MessageSource messageSource) {

    }
}
