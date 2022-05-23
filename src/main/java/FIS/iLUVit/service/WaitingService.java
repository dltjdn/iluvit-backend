package FIS.iLUVit.service;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final PtDateRepository ptDateRepository;
    private final ParentRepository parentRepository;

    public void register(Long userId, Long ptDateId) {
        // 학부모 조회
        Parent parent = parentRepository.findByIdAndFetchPresentation(userId)
                .orElseThrow(() -> new UserException("해당 사용자가 존재하지 않습니다"));
        // 설명회 회차 조회
        PtDate ptDate = ptDateRepository.findByIdJoinWaiting(ptDateId)
                .orElseThrow(() -> new PresentationException("해당 설명회는 존재하지 않습니다"));

    }
}
