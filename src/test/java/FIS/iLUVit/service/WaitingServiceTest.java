package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.WaitingErrorResult;
import FIS.iLUVit.exception.WaitingException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.WaitingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {
    @Nested
    @DisplayName("설명회 등록")
    class register{

        // TODO 설명회 신청 기간 지남

        // TODO 설명회 대기 신청 이미 함

        // TODO 설명회 신청했을 경우 대기신청 불가능

        // TODO 설명회 신청 인원 초과X (설명회 신청인원 초과 아닐시 오류)

        // TODO 설명회 대기 신청 성공
    }

    @Nested
    @DisplayName("설명회 대기 취소")
    class 설명회대기취소{

        // TODO 대기 조회 결과 없음

        // TODO 대기 취소 성공

        // TODO 대기 취소 성공
    }
}