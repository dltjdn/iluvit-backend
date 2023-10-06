package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
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

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;
    private final ParticipationRepository participationRepository;
    private final AlarmService alarmService;

    /**
     * 설명회 회차에 대기를 신청합니다
     */
    public void waitingParticipation(Long userId, Long ptDateId) {

        // 잘못된 ptDateId로 요청 시 오류 반환
        PtDate ptDate = getPtDate(ptDateId);

        // 설명회 신청기간이 지났을경우 error throw
        ptDate.getPresentation().checkCanRegister();

        // 대기 등록을 이미 했을 경우 error Throw
        waitingRepository.findByPtDate(ptDate).forEach(waiting -> {
            if(waiting.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_ON_WAIT);
        });

        // 설명회 인원이 가득 차지 않았을 경우 error Throw
        ptDate.checkCanNotRegister();

        // 설명회를 이미 신청 했을 경우 error Throw
        List<Participation> participants = participationRepository.findByPtDateAndStatus(ptDate, Status.JOINED);
        participants.forEach(participation -> {
            if(participation.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED);
        });

        Parent parent = getParent(userId);
        int waitingOrder = ptDate.getWaitingCnt() + 1;

        Waiting waiting = Waiting.createWaiting(waitingOrder, parent, ptDate);

        ptDate.increaseWaitingCnt();

        waitingRepository.save(waiting);
    }

    /**
     * 설명회 대기를 취소하고 대기 순서를 변경합니다
     */
    public void cancelParticipation(Long userId, Long waitingId) {

        // 잘못된 waitingId로 요청 시 오류 반환
        if(waitingId < 0)
            throw new WaitingException(WaitingErrorResult.WRONG_WAITING_ID_REQUEST);

        // 검색 결과 없으면 오류 반환
        Parent parent = getParent(userId);
        Waiting waiting = waitingRepository.findByIdAndParent(waitingId, parent)
                .orElseThrow(() -> new WaitingException(WaitingErrorResult.WAITING_NOT_FOUND));

        PtDate ptDate = waiting.getPtDate();
        ptDate.decreaseWaitingCnt();
        waitingRepository.updateWaitingOrder(ptDate, waiting.getWaitingOrder());
        waitingRepository.delete(waiting);
    }


    /**
     * 해당 회차의 대기들를 삭제한다
     */
    public void updateWaitingOrdersAndDeleteWaitings(List<Waiting> waitings, int capacityNum, PtDate ptDate ){
        List<Long> waitingIds = waitings.stream()
                .map(Waiting::getId)
                .collect(toList());

        // 수용 인원들 waiting 에서 삭제
        waitingRepository.deleteByIdIn(waitingIds);

        // 수용 외의 인원들 order 감소
        waitingRepository.updateWaitingOrderForPtDateChange(capacityNum, ptDate);

    }

    /**
     * 해당 회차의 첫번째 대기자를 삭제하고, 그 대기자에게 설명회 참여 알림을 보낸다
     */
    public Waiting updateWaitingOrderAndDeleteWaiting(PtDate ptDate, Presentation presentation){
        Waiting waiting = waitingRepository.findFirstByPtDateOrderByWaitingOrderAsc(ptDate).orElse(null);

        if (waiting == null) {
            ptDate.resetWaitingCnt();
            return waiting;
        }
        waitingRepository.updateWaitingOrderForPtDateChange(1, ptDate);
        waitingRepository.delete(waiting);

        alarmService.sendParticipateAlarm(waiting, presentation); // 설명회 신청 알림 전송
        return waiting;
    }

    /**
     * 예외처리 - 존재하는 설명회 회차인가
     */
    private PtDate getPtDate(Long ptDateId) {
        if(ptDateId < 0)
            throw new PresentationException(PresentationErrorResult.INVALID_PTDATE_ID);

        return ptDateRepository.findById(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PTDATE_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

    }

}