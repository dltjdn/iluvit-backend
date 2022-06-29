package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import FIS.iLUVit.service.ParticipationService;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationCancelEventHandler {

    private final WaitingService waitingService;
    private final ParticipationRepository participationRepository;
    private final WaitingRepository waitingRepository;

    @EventListener
    public void changeWaitingToParticipation(ParticipationCancelEvent event){
        Waiting waiting = waitingService.findFirstOrderWaiting(event.getPtDate());
        Presentation presentation = event.getPresentation();
        PtDate ptDate = event.getPtDate();
        Participation waitingToParticipate = Waiting.whenParticipationCanceled(waiting, presentation);
        participationRepository.save(waitingToParticipate);
        waitingRepository.delete(waiting);
    }
}
