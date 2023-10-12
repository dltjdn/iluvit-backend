package FIS.iLUVit.domain.participation.service;

import FIS.iLUVit.domain.alarm.repository.AlarmRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.participation.domain.Participation;
import FIS.iLUVit.domain.participation.dto.ParticipationCreateRequest;
import FIS.iLUVit.domain.participation.exception.ParticipationErrorResult;
import FIS.iLUVit.domain.participation.exception.ParticipationException;
import FIS.iLUVit.domain.participation.repository.ParticipationRepository;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.presentation.exception.PresentationErrorResult;
import FIS.iLUVit.domain.presentation.exception.PresentationException;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import FIS.iLUVit.domain.ptdate.repository.PtDateRepository;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.waiting.repository.WaitingRepository;
import FIS.iLUVit.domain.alarm.domain.Alarm;
import FIS.iLUVit.domain.common.domain.NotificationTitle;
import FIS.iLUVit.domain.participation.dto.ParticipationResponse;
import FIS.iLUVit.domain.alarm.domain.PresentationFullAlarm;
import FIS.iLUVit.domain.participation.domain.Status;
import FIS.iLUVit.domain.participation.dto.ParticipationWithStatusResponse;
import FIS.iLUVit.domain.alarm.event.ParticipationCancelEvent;
import FIS.iLUVit.domain.alarm.AlarmUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.participation.domain.Status.CANCELED;
import static FIS.iLUVit.domain.participation.domain.Status.JOINED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // 기본 Required 트랜잭션들 다 엮인다.
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AlarmRepository alarmRepository;
    private final WaitingRepository waitingRepository;

    /**
     * 설명회 신청
     */
    public void registerParticipation(Long userId, ParticipationCreateRequest request) {
        Long ptDateId = request.getPtDateId();
        // 잘못된 설명회 회차 id일 경우
        PtDate ptDate = ptDateRepository.findById(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PTDATE_NOT_FOUND));

        // 설명회 신청기간이 지났을경우
        if(LocalDate.now().isAfter(ptDate.getPresentation().getEndDate()))
            // 핵심 비지니스 로직 => 설명회 canRegister
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_EXPIRED);

        // 설명회 수용인원이 초과일 경우
        if(ptDate.getParticipantCnt() >= ptDate.getAblePersonNum())
            throw new PresentationException(PresentationErrorResult.CAPACITY_EXCEEDED);

        List<Participation> participations = ptDate.getParticipations();
        Presentation presentation = ptDate.getPresentation();

        // 학부모 조회
        Parent parent = parentRepository.getById(userId);

        participations.forEach(participation -> {
            if(participation.getStatus().equals(JOINED) && participation.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED);
        });

        // 설명회 등록
        participationRepository.save(Participation.createParticipation(parent, presentation, ptDate, participations));

        if(ptDate.getAblePersonNum() <= ptDate.getParticipantCnt()){
            teacherRepository.findByCenter(presentation.getCenter()).forEach((teacher) -> {
                Alarm alarm = new PresentationFullAlarm(teacher, presentation, presentation.getCenter());
                alarmRepository.save(alarm);
                AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
            });
        }
    }

    /**
     * 설명회 취소 ( 대가자 있을 경우 자동 합류 )
     */
    public void cancelParticipation(Long userId, Long participationId) {
        if(participationId < 0)
            throw new ParticipationException(ParticipationErrorResult.WRONG_PARTICIPATION_ID_REQUEST);

        Parent parent = getParent(userId);

        Participation participation = participationRepository.findByIdAndStatusAndParent(participationId, JOINED, parent)
                .orElseThrow(() -> new ParticipationException(ParticipationErrorResult.PARTICIPATION_NOT_FOUND));

        participation.cancelParticipation(); // ptDate cnt 값을 1줄여야 한다.

        PtDate ptDate = participation.getPtDate();

        if(ptDate.checkHasWaiting()){
            eventPublisher.publishEvent(new ParticipationCancelEvent(ptDate.getPresentation(), ptDate)); // 이벤트 리스너 호출
        }
    }


    /**
     * 신청한/취소한 설명회 전체 조회
     */
    public Map<Status, List<ParticipationResponse>> findAllParticipationByUser(Long userId) {
        // 학부모 조회
        Parent parent = getParent(userId);

        List<ParticipationResponse> participationResponses = participationRepository.findByParent(parent).stream()
                .map(ParticipationResponse::from)
                .collect(Collectors.toList());

        participationResponses.addAll(
                parent.getWaitings().stream()
                .map(ParticipationResponse::of)
                .collect(Collectors.toList())
        );

        return participationResponses.stream()
                .collect(Collectors.groupingBy(ParticipationResponse::getStatus));
    }

    /**
     * 신청한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findRegisterParticipationByUser(Long userId, Pageable pageable){
        Parent parent = getParent(userId);

        Slice<ParticipationResponse> participationDtos = participationRepository.findByParentAndStatus(parent, JOINED, pageable)
                .map(ParticipationResponse::from);

        return participationDtos;
    }

    /**
     * 신청을 취소한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findCancelParticipationByUser(Long userId, Pageable pageable){
        Parent parent = getParent(userId);

        Slice<ParticipationResponse> participationDtos = participationRepository.findByParentAndStatus(parent, CANCELED, pageable)
                .map(ParticipationResponse::from);

        return participationDtos;
    }

    /**
     * 대기를 신청한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findWaitingParticipationByUser(Long userId, Pageable pageable){
        Parent parent = getParent(userId);

        Slice<ParticipationResponse> participationDtos = waitingRepository.findByParent(parent, pageable)
                .map(ParticipationResponse::of);

        return participationDtos;
    }


    /**
     * 설명회 참여정보를 저장한다
     */
    public void saveParticipation(Parent parent, Presentation presentation,PtDate ptDate){
        List<Participation> participations = participationRepository.findByPtDate(ptDate);

        Participation paticipation = Participation.createParticipation(parent, presentation, ptDate, participations);

        participationRepository.save(paticipation);
    }

    /**
     *  신청되어있는 설명회 신청 목록에서 빠지게 하기 ( 설명회 신청 삭제 )
     */
    public void deleteParticipationByWithdraw(Parent parent){
        participationRepository.findByParent(parent).forEach(participation -> {
            participationRepository.deleteById(participation.getId());
        });
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

}
