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

    @Mock
    WaitingRepository waitingRepository;
    @Mock
    PtDateRepository ptDateRepository;
    @Mock
    ParentRepository parentRepository;
    @Mock
    ParticipationRepository participationRepository;

    @InjectMocks
    WaitingService target;

    @Nested
    @DisplayName("설명회 등록")
    class register{

        @Test
        @DisplayName("[Error] 설명회 신청 기간 지남")
        public void 설명회_신청_기간_지남() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createInvalidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Mockito.doReturn(Optional.of(ptDate)).when(ptDateRepository).findByIdWith(any(Long.class));

            //when
            PresentationException presentationException = assertThrows(PresentationException.class, () -> target.register(1L, 1L));

            //then
            assertThat(presentationException.getErrorResult()).isEqualTo(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);
        }

        @Test
        @DisplayName("[Error] 대기 신청을 이미 함")
        public void 설명회_대기_신청_이미_함() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Parent parent = createParent(1L);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Waiting waiting = createWaiting(ptDate, parent, 1);
            ptDate.getWaitings().add(waiting);
            createCancelParticipation(ptDate, parent);
//            createWaiting()

            Mockito.doReturn(Optional.of(ptDate)).when(ptDateRepository).findByIdWith(ptDate.getId());
            //when

            PresentationException result = assertThrows(PresentationException.class, () ->
                    target.register(1L, ptDate.getId())
            );
            //then

            assertThat(result.getErrorResult()).isEqualTo(PresentationErrorResult.ALREADY_WAITED_IN);
        }

        @Test
        @DisplayName("[Error] 설명회 신청을 이미 함")
        public void 설명회_신청을_했을_경우_대기신청_불가능() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Parent parent = createParent(1L);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Participation joinParticipation = createJoinParticipation(ptDate, parent);
            ptDate.getParticipations().add(joinParticipation);

            Mockito.doReturn(Optional.of(ptDate)).when(ptDateRepository).findByIdWith(1L);
            Mockito.doReturn(List.of(joinParticipation)).when(participationRepository).findByPtDateAndStatusJOINED(1L);

            //when
            PresentationException presentationException = assertThrows(PresentationException.class, () -> target.register(1L, 1L));

            //then
            assertThat(presentationException.getErrorResult()).isEqualTo(PresentationErrorResult.ALREADY_PARTICIPATED_IN);
        }

        @Test
        @DisplayName("[error] 설명회 신청인원 초과 아닐시 오류")
        public void 설명회신청인원초과X() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Parent parent = createParent(1L);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);


            Mockito.doReturn(Optional.of(ptDate)).when(ptDateRepository).findByIdWith(1L);

            //when
            PresentationException presentationException = assertThrows(PresentationException.class, () -> target.register(1L, 1L));

            //then
            assertThat(presentationException.getErrorResult()).isEqualTo(PresentationErrorResult.PRESENTATION_NOT_OVERCAPACITY);
        }

        @Test
        @DisplayName("[success] 대기 신청 성공")
        public void 대기_신청_성공() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Parent parent = createParent(1L);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Integer waitingOrder = ptDate.getWaitingCnt();
            Participation joinParticipation = createJoinParticipation(ptDate, parent);
            ptDate.getParticipations().add(joinParticipation);
            Waiting waiting = createWaiting(ptDate, parent, ptDate.getWaitingCnt());
            Mockito.doReturn(Optional.of(ptDate)).when(ptDateRepository).findByIdWith(1L);
            Mockito.doReturn(List.of(joinParticipation)).when(participationRepository).findByPtDateAndStatusJOINED(1L);
            Mockito.doReturn(parent).when(parentRepository).getById(2L);
            Mockito.doReturn(waiting).when(waitingRepository).save(any(Waiting.class));
            //when

            Waiting result = target.register(2L, 1L);

            //then
            assertThat(result.getParent()).isEqualTo(parent);
            assertThat(result.getWaitingOrder()).isEqualTo(waitingOrder + 1);
            assertThat(ptDate.getWaitingCnt()).isEqualTo(waitingOrder + 1);
            assertThat(result.getPtDate()).isEqualTo(ptDate);

        }
    }

    @Nested
    @DisplayName("설명회 대기 취소")
    class 설명회대기취소{
        @Test
        @DisplayName("[error] 대기 조회 결과 없음")
        public void 대기조회결과없음() throws Exception {
            //given
            Mockito.doReturn(Optional.ofNullable(null))
                    .when(waitingRepository).findByIdWithPtDate(1L, 1L);
            //when
            WaitingException result = assertThrows(WaitingException.class, () -> target.cancel(1L, 1L));
            //then
            assertThat(result.getWaitingErrorResult()).isEqualTo(WaitingErrorResult.NO_RESULT);
        }

        @Test
        @DisplayName("[success] 대기 취소 성공")
        public void 대기취소성공() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Integer waitingCnt = ptDate.getWaitingCnt();
            Participation participation = createCancelParticipation(ptDate, parent);
            Waiting waiting1 = createWaiting(ptDate, parent, 1);
            Waiting waiting2 = createWaiting(ptDate, parent, 2);
            Waiting waiting3 = createWaiting(ptDate, parent, 3);

            Mockito.doReturn(Optional.of(waiting2)).when(waitingRepository).findByIdWithPtDate(1L, 1L);

            //when
            Long cancel = target.cancel(1L, 1L);

            //then
            assertThat(ptDate.getWaitingCnt()).isEqualTo(waitingCnt - 1);

        }

    }



}