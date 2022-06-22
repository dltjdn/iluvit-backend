package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.service.AlarmUtils;
import FIS.iLUVit.service.AlarmUtils.Mode;
import org.springframework.context.MessageSource;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CenterApprovalAlarm extends Alarm{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Center center;

    @Override
    public Alarm createMessage(MessageSource messageSource, Mode mode) {
        return null;
    }
}
