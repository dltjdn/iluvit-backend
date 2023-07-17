package FIS.iLUVit.service;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.*;
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

    /**
     * 설명회 회차에 대기를 신청합니다
     */
    public void waitingParticipation(Long userId, Long ptDateId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        // 잘못된 ptDateId로 요청 시 오류 반환
        if(ptDateId < 0)
            throw new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST);

        PtDate ptDate = ptDateRepository.findById(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST));

        // 설명회 신청기간이 지났을경우 error throw
        if(LocalDate.now().isAfter(ptDate.getPresentation().getEndDate()))
            // 핵심 비지니스 로직 => 설명회 canRegister
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);

        // 대기 등록을 이미 했을 경우 error Throw
        waitingRepository.findByPtDate(ptDate).forEach(waiting -> {
            if(waiting.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_WAITED_IN);
        });

        // 설명회 인원이 가득 차지 않았을 경우 error Throw
        if(ptDate.getAblePersonNum() > ptDate.getParticipantCnt())
            throw new PresentationException(PresentationErrorResult.PRESENTATION_NOT_OVERCAPACITY);

        // 설명회를 이미 신청 했을 경우 error Throw
        Status status = Status.JOINED;
        List<Participation> participants = participationRepository.findByPtDateIdAndStatus(ptDateId, status);
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

        waitingRepository.save(waiting);
    }

    /**
     * 설명회 대기를 취소하고 대기 순서를 변경합니다
     */
    public void cancelParticipation(Long userId, Long waitingId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        // 잘못된 waitingId로 요청 시 오류 반환
        if(waitingId < 0)
            throw new WaitingException(WaitingErrorResult.WRONG_WAITINGID_REQUEST);

        // 검색 결과 없으면 오류 반환
        Parent parent = parentRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Waiting waiting = waitingRepository.findByIdAndParent(waitingId, parent)
                .orElseThrow(() -> new WaitingException(WaitingErrorResult.NO_RESULT));

        PtDate ptDate = waiting.getPtDate();
        ptDate.decreaseWaitingCnt();
        waitingRepository.updateWaitingOrder(ptDate, waiting.getWaitingOrder());
        waitingRepository.delete(waiting);
    }

}