package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // 기본 Required 트랜잭션들 다 엮인다.
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final AlarmUtils alarmUtils;

    public Long register(Long userId, Long ptDateId) {


        // 잘못된 설명회 회차 id일 경우 error throw
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST));
        System.out.println(alarmUtils.toString());
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
            if(participation.getParent().getId() == userId)
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED_IN);
        });

        // 설명회 등록
        Participation participation = participationRepository.save(
                Participation.createAndRegister(parent, presentation, ptDate, participations)
        );

        if(ptDate.getAblePersonNum() >= ptDate.getParticipantCnt()){
            userRepository.findTeacherByCenter(presentation.getCenter()).forEach((user) -> {
                AlarmUtils.publishAlarmEvent(new PresentationFullAlarm(user, presentation, presentation.getCenter()));
            });
        }

        return participation.getId();
    }

    public Long cancel(Long userId, Long participationId) {
        // 학부모 조회
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        // 참여 조회 참여를 취소한 경우에는 ptDate 가 null 값이라 안나온다.
        Participation participation = participationRepository.findByIdAndJoinPresentation(participationId)
                .orElseThrow(() -> new PresentationException("올바르지 않은 ptDate id 입니다"));
        // 설명회 회차 조회
        PtDate ptDate = participation.getPtDate();
        // 학부모가 참여를 신청한게 맞는지 조회
        if (!participation.getParent().equals(parent))
            throw new PresentationException("해당 사용자가 설명회 신청한적 없습니다.");
        participation.cancel();
        // 참여를 취소할 경우 대기자 중에서 가장 높은 순번이 자동으로 등록 됨
        if(ptDate.hasWaiting()){
            publisher.publishEvent(new ParticipationCancelEvent(ptDate.getPresentation(), ptDate, null)); // 이벤트 리스너 호출
        }
        return participationId;
    }

    public Map<Status, List<MyParticipationsDto>> getMyParticipation(Long userId) {
        // 학부모 조회
        Parent parent = parentRepository.findMyParticipation(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        parentRepository.findMyWaiting(userId);

        List<MyParticipationsDto> myParticipationsDtos = parent.getParticipations().stream()
                .map(participation -> MyParticipationsDto.createDto(participation))
                .collect(Collectors.toList());

        myParticipationsDtos.addAll(
                parent.getWaitings().stream()
                .map(waiting -> MyParticipationsDto.createDto(waiting))
                .collect(Collectors.toList())
        );

        return myParticipationsDtos.stream()
                .collect(Collectors.groupingBy(myParticipationsDto -> myParticipationsDto.getStatus()));
    }
}
