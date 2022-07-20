package FIS.iLUVit.event;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import lombok.Getter;

@Getter
public class ParticipationCancelEvent {

    private final Presentation presentation;
    private final PtDate ptDate;

    public ParticipationCancelEvent(Presentation presentation, PtDate ptDate) {
        this.presentation = presentation;
        this.ptDate = ptDate;
    }
}
