package FIS.iLUVit.event;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import lombok.Getter;

@Getter
public class ParticipationCancelEvent {

    private Presentation presentation;
    private PtDate ptDate;
    private Parent parent;

    public ParticipationCancelEvent(Presentation presentation, PtDate ptDate, Parent parent) {
        this.presentation = presentation;
        this.ptDate = ptDate;
        this.parent = parent;
    }
}
