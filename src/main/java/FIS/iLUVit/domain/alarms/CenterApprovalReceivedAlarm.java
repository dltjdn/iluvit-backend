package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.MessageUtils;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
public class CenterApprovalReceivedAlarm extends Alarm{

    public CenterApprovalReceivedAlarm(User user) {
        super(user);
        this.mode = MessageUtils.CENTER_APPROVAL_RECEIVED;
        message = MessageUtils.getMessage(mode, null);
    }
}
