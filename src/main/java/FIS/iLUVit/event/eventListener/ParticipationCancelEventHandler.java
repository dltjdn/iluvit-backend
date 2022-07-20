package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationCancelEventHandler {

    private final ParticipationRepository participationRepository;
    private final WaitingRepository waitingRepository;

    @EventListener
    public void changeWaitingToParticipation(ParticipationCancelEvent event){
        Presentation presentation = event.getPresentation();
        PtDate ptDate = event.getPtDate();
        Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate);
        if (waiting == null) {
            ptDate.resetWaitingCnt();
            return;
        }
        Participation waitingToParticipate = Waiting.whenParticipationCanceled(waiting, presentation);
        participationRepository.save(waitingToParticipate);
        waitingRepository.updateWaitingOrderForPtDateChange(waiting.getWaitingOrder(), ptDate);
        waitingRepository.delete(waiting);
    }
}
