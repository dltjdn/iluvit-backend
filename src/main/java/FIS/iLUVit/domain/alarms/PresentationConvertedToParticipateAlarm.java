package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.service.MessageUtils;
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
        this.mode = MessageUtils.PRESENTATION_WAITING_TO_PARTICIPATE;
        this.message = MessageUtils.getMessage(mode, args);
    }

}
