package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.participation.ParticipationResponse;
import FIS.iLUVit.domain.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.dto.participation.ParticipationWithStatusResponse;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
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

import static FIS.iLUVit.domain.enumtype.Status.CANCELED;
import static FIS.iLUVit.domain.enumtype.Status.JOINED;

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
    public void registerParticipation(Long userId, Long ptDateId) {
        // 잘못된 설명회 회차 id일 경우
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST));

        // 설명회 신청기간이 지났을경우
        if(LocalDate.now().isAfter(ptDate.getPresentation().getEndDate()))
            // 핵심 비지니스 로직 => 설명회 canRegister
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);

        // 설명회 수용인원이 초과일 경우
        if(ptDate.getParticipantCnt() >= ptDate.getAblePersonNum())
            throw new PresentationException(PresentationErrorResult.PRESENTATION_OVERCAPACITY);

        List<Participation> participations = ptDate.getParticipations();
        Presentation presentation = ptDate.getPresentation();

        // 학부모 조회
        Parent parent = parentRepository.getById(userId);

        participations.forEach(participation -> {
            if(participation.getStatus().equals(JOINED) && participation.getParent().getId().equals(userId))
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED_IN);
        });

        // 설명회 등록
        participationRepository.save(Participation.createAndRegister(parent, presentation, ptDate, participations));

        if(ptDate.getAblePersonNum() <= ptDate.getParticipantCnt()){
            teacherRepository.findByCenter(presentation.getCenter()).forEach((teacher) -> {
                Alarm alarm = new PresentationFullAlarm(teacher, presentation, presentation.getCenter());
                alarmRepository.save(alarm);
                String type = "아이러빗";
                AlarmUtils.publishAlarmEvent(alarm, type);
            });
        }
    }

    /**
     * 설명회 취소 ( 대가자 있을 경우 자동 합류 )
     */
    public void cancelParticipation(Long userId, Long participationId) {
        if(participationId < 0)
            throw new ParticipationException(ParticipationErrorResult.WRONG_PARTICIPATIONID_REQUEST);

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Participation participation = participationRepository.findByIdAndStatusAndParent(participationId, JOINED, parent)
                .orElseThrow(() -> new ParticipationException(ParticipationErrorResult.NO_RESULT));

        participation.cancel(); // ptDate cnt 값을 1줄여야 한다.

        PtDate ptDate = participation.getPtDate();

        if(ptDate.hasWaiting()){
            eventPublisher.publishEvent(new ParticipationCancelEvent(ptDate.getPresentation(), ptDate)); // 이벤트 리스너 호출
        }
    }

    /**
     * 신청한/취소한 설명회 전체 조회
     */
    public List<ParticipationWithStatusResponse> findAllParticipationByUser(Long userId) {
        // 학부모 조회
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(()-> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<ParticipationResponse> participationResponses = participationRepository.findByParent(parent).stream()
                .map(ParticipationResponse::createDtoByParticipation)
                .collect(Collectors.toList());

        participationResponses.addAll(
                parent.getWaitings().stream()
                .map(ParticipationResponse::createDtoByWaiting)
                .collect(Collectors.toList())
        );

        Map<Status, List<ParticipationResponse>> statusParticipationMap = participationResponses.stream()
                .collect(Collectors.groupingBy(ParticipationResponse::getStatus));

        List<ParticipationWithStatusResponse> participationWithStatusResponses = new ArrayList<>();

        statusParticipationMap.forEach((status, participationDtoList)-> {
            participationWithStatusResponses.add(new ParticipationWithStatusResponse(status, participationDtoList));
        });

        return participationWithStatusResponses;
    }

    /**
     * 신청한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findRegisterParticipationByUser(Long userId, Pageable pageable){
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Slice<ParticipationResponse> participationDtos = participationRepository.findByParentAndStatus(parent, JOINED, pageable)
                .map(ParticipationResponse::createDtoByParticipation);

        return participationDtos;
    }

    /**
     * 신청을 취소한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findCancelParticipationByUser(Long userId, Pageable pageable){
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Slice<ParticipationResponse> participationDtos = participationRepository.findByParentAndStatus(parent, CANCELED, pageable)
                .map(ParticipationResponse::createDtoByParticipation);

        return participationDtos;
    }

    /**
     * 대기를 신청한 설명회 전체 조회
     */
    public Slice<ParticipationResponse> findWaitingParticipationByUser(Long userId, Pageable pageable){
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Slice<ParticipationResponse> participationDtos = waitingRepository.findByParent(parent, pageable)
                .map(ParticipationResponse::createDtoByWaiting);

        return participationDtos;
    }

}
