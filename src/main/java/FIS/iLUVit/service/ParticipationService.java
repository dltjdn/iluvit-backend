package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.participation.ParticipationDto;
import FIS.iLUVit.domain.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.enumtype.Status;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final UserRepository userRepository;

    public Long registerParticipation(Long userId, Long ptDateId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        // 잘못된 설명회 회차 id일 경우 error throw
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST));

        // 설명회 신청기간이 지났을경우 error throw
        if(LocalDate.now().isAfter(ptDate.getPresentation().getEndDate()))
            // 핵심 비지니스 로직 => 설명회 canRegister
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);

        // 설명회 수용인원이 초과일 경우 error throw
        // 이름 잘만들기
        // isCapacityOver(
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
        Participation participation = participationRepository.save(
                Participation.createAndRegister(parent, presentation, ptDate, participations)
        );

        if(ptDate.getAblePersonNum() <= ptDate.getParticipantCnt()){
            teacherRepository.findByCenter(presentation.getCenter()).forEach((teacher) -> {
                Alarm alarm = new PresentationFullAlarm(teacher, presentation, presentation.getCenter());
                alarmRepository.save(alarm);
                AlarmUtils.publishAlarmEvent(alarm);
            });
        }

        return participation.getId();
    }

    public Long cancelParticipation(Long userId, Long participationId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        if(participationId < 0)
            throw new ParticipationException(ParticipationErrorResult.WRONG_PARTICIPATIONID_REQUEST);

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Participation participation = participationRepository.findByIdAndStatusAndParent(participationId, JOINED, parent)
                .orElseThrow(() -> new ParticipationException(ParticipationErrorResult.NO_RESULT));
        // ptDate cnt 값을 1줄여야 한다.
        participation.cancel();
        PtDate ptDate = participation.getPtDate();
        if(ptDate.hasWaiting()){
            eventPublisher.publishEvent(new ParticipationCancelEvent(ptDate.getPresentation(), ptDate)); // 이벤트 리스너 호출
        }

        return participationId;
    }

    public Map<Status, List<ParticipationDto>> findAllParticipationByUser(Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        // 학부모 조회
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(()-> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<ParticipationDto> participationDtos = participationRepository.findByParent(parent).stream()
                .map(ParticipationDto::createDto)
                .collect(Collectors.toList());

        participationDtos.addAll(
                parent.getWaitings().stream()
                .map(ParticipationDto::createDto)
                .collect(Collectors.toList())
        );

        return participationDtos.stream()
                .collect(Collectors.groupingBy(ParticipationDto::getStatus));
    }

    public Slice<ParticipationDto> findRegisterParticipationByUser(Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        parentRepository.findMyJoinParticipation(userId, pageable);

        //TODO dto 로직 옮기다
        new ParticipationDto()
    }

    public Slice<ParticipationDto> findCancelParticipationByUser(Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        //TODO dto 로직 옮기기
        return parentRepository.findMyCancelParticipation(userId, pageable);
    }

    public Slice<ParticipationDto> findWaitingParticipationByUser(Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        //TODO dto 로직 옮기기
        return parentRepository.findMyWaiting(userId, pageable);
    }

}
