package FIS.iLUVit.event;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.PtDate;
import lombok.Getter;

@Getter
public class ParticipationCancelEvent {

    private PtDate ptDate;
    private Parent parent;

    public ParticipationCancelEvent(PtDate ptDate, Parent parent) {
        this.ptDate = ptDate;
        this.parent = parent;
    }
}
