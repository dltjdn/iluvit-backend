package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Center;

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
    protected Alarm setRedirectUrl() {
        return null;
    }
}
