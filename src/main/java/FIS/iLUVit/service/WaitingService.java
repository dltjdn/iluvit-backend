package FIS.iLUVit.service;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;
    private final ParticipationRepository participationRepository;

    public Long register(Long userId, Long ptDateId) {
        // 학부모 조회
        Parent parent = parentRepository.findByIdAndFetchPresentation(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        // 설명회 회차 조회
        PtDate ptDate = ptDateRepository.findByIdJoinWaiting(ptDateId)
                .orElseThrow(() -> new PresentationException("해당 설명회는 존재하지 않습니다"));
        List<Participation> participations = participationRepository.findByptDateAndStatus(ptDate);
        Waiting waiting = Waiting.createAndRegister(parent, ptDate, participations);
        waitingRepository.save(waiting);
        return waiting.getId();
    }


    public Waiting findFirstOrderWaiting(PtDate ptDate) {
        waitingRepository.updateWaitingForParticipationCancel(ptDate);
        return waitingRepository.findMinWaitingOrder(ptDate)
                .orElseThrow(() -> new PresentationException("DB 적합성 오류 발생"));
    }

    public Long cancel(Long waitingId) {
        Waiting waiting = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new PresentationException("올바르지 않은 대기취소 입니다."));
        Integer waitingOrder = waiting.getWaitingOrder();
        waitingRepository.updateWaitingOrderForWaitCancel(waitingOrder);
        waitingRepository.delete(waiting);
        return waitingId;
    }
}
