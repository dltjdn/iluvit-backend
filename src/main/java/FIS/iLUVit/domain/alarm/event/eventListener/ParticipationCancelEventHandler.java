package FIS.iLUVit.domain.alarm.event.eventListener;

import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import FIS.iLUVit.domain.alarm.event.ParticipationCancelEvent;
import FIS.iLUVit.domain.participation.service.ParticipationService;
import FIS.iLUVit.domain.waiting.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
