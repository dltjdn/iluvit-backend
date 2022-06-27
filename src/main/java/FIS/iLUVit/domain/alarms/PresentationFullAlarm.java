package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.MessageUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class PresentationFullAlarm extends Alarm{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentationId")
    private Presentation presentation;

    public PresentationFullAlarm(User user, Presentation presentation) {
        super(user);
        this.mode = MessageUtils.PRESENTATION_APPLICANTS_FULL;
        this.presentation = presentation;
        message = MessageUtils.getMessage(mode, null);
    }


}
