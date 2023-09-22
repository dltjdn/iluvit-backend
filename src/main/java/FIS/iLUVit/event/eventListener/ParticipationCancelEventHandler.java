package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.service.ParticipationService;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParticipationCancelEventHandler {

    private final ParticipationService participationService;
    private final WaitingService waitingService;

    @EventListener
    public void changeWaitingToParticipation(ParticipationCancelEvent event){
        Presentation presentation = event.getPresentation();
        PtDate ptDate = event.getPtDate();

        Waiting waiting = waitingService.updateWaitingOrderAndDeleteWaiting(ptDate, presentation);

        ptDate.cancelWaitingForAcceptingParticipation();

        participationService.saveParticipation(waiting.getParent(), presentation, ptDate);

    }
}
