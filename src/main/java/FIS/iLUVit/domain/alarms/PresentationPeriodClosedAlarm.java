package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.MessageUtils;
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
        this.mode = MessageUtils.PRESENTATION_CLOSED;
        this.presentation = presentation;
        message = MessageUtils.getMessage(mode, null);
    }

}
