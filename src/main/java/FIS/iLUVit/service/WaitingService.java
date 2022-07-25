package FIS.iLUVit.service;

import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.WaitingErrorResult;
import FIS.iLUVit.exception.WaitingException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public Waiting register(Long userId, Long ptDateId) {
        // 학부모 조회
        PtDate ptDate = ptDateRepository.findByIdWith(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST));

        // 설명회 신청기간이 지났을경우 error throw
        if(LocalDate.now().isAfter(ptDate.getPresentation().getEndDate()))
            // 핵심 비지니스 로직 => 설명회 canRegister
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);

        // 대기 등록을 이미 했을 경우 error Throw
        ptDate.getWaitings().forEach(waiting -> {
            if(waiting.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_WAITED_IN);
        });

        // 설명회 인원이 가득 차지 않았을 경우 error Throw
        if(ptDate.getAblePersonNum() > ptDate.getParticipantCnt())
            throw new PresentationException(PresentationErrorResult.PRESENTATION_NOT_OVERCAPACITY);

        // 설명회를 이미 신텅 했을 경우 error Throw
        List<Participation> participants = participationRepository.findByPtDateAndStatusJOINED(ptDateId);
        participants.forEach(participation -> {
            if(participation.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED_IN);
        });

        Waiting waiting = Waiting.builder()
                .ptDate(ptDate)
                .parent(parentRepository.getById(userId))
                .waitingOrder(ptDate.getWaitingCnt() + 1)
                .build();

        ptDate.increaseWaitingCnt()
                .acceptWaiting(waiting);

        Waiting saved = waitingRepository.save(waiting);

        return waiting;
    }

    public Long cancel(Long waitingId, Long userId) {

        // 검색 결과 없으면 오류 반환
        Waiting waiting = waitingRepository.findByIdWithPtDate(waitingId, userId)
                .orElseThrow(() -> new WaitingException(WaitingErrorResult.NO_RESULT));

        PtDate ptDate = waiting.getPtDate();
        ptDate.decreaseWaitingCnt();
        waitingRepository.updateWaitingOrder(ptDate, waiting.getWaitingOrder());
        waitingRepository.delete(waiting);

        return waitingId;
    }


}
