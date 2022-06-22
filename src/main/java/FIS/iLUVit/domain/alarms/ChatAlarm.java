package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.User;
import org.springframework.context.MessageSource;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ChatAlarm extends Alarm{

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Override
    public Alarm createMessage(MessageSource messageSource) {
        return null;
    }
}
