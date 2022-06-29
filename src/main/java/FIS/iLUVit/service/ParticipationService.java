package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long register(Long userId, Long ptDateId) {
        // 학부모 조회
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        // 설명회 세부 조회
        log.info("{}", ptDateId);
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException("해당 설명회는 존재하지 않습니다"));
        List<Participation> participations = ptDate.getParticipations();
        Presentation presentation = ptDate.getPresentation();
        // 설명회 등록
        Participation participation = Participation.createAndRegister(parent, presentation, ptDate, participations);
        participationRepository.save(participation);
        if(ptDate.getAblePersonNum() >= ptDate.getParticipantCnt()){
            userRepository.findTeacherByCenter(presentation.getCenter()).forEach((user) -> {
                AlarmUtils.publishAlarmEvent(new PresentationFullAlarm(user, presentation));
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
        Parent parent = parentRepository.findByIdAndFetchPresentation(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));

        List<Participation> participations = parent.getParticipations();
        List<MyParticipationsDto> myParticipationsDtos = participations.stream()
                .map(participation -> MyParticipationsDto.createDto(participation))
                .collect(Collectors.toList());

        return myParticipationsDtos.stream()
                .collect(Collectors.groupingBy(myParticipationsDto -> myParticipationsDto.getStatus()));
    }
}
