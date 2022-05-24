package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;
    private final WaitingRepository waitingRepository;

    public Long register(Long userId, Long ptDateId) {
        // 학부모 조회
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        // 설명회 세부 조회
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException("해당 설명회는 존재하지 않습니다"));
        // 설명회 인원 초과가 되었으면 신청 불가
        if (!ptDate.canRegister()) {
            throw new PresentationException("설명회 수용가능 인원이 초과 되었습니다 대기자로 등록해 주세요");
        }
        // 해당 학부모가 설명회를 등록한적 있는가?
        Participation.hasRegistered(ptDate.getParticipations(), parent);
        // 설명회 등록
        Participation participation = Participation.createAndRegister(parent, ptDate);
        participationRepository.save(participation);
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
        Integer waitingCnt = ptDate.getWaitingCnt();
        if(waitingCnt != null || waitingCnt > 0){
            waitingRepository.updateWaitingForParticipationCancel(ptDate);
            Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate)
                    .orElseThrow(() -> new PresentationException("DB 적합성 오류 발생"));
            Participation waitingToParticipate = Waiting.whenParticipationCanceled(waiting);
            participationRepository.save(waitingToParticipate);
            waitingRepository.delete(waiting);
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
