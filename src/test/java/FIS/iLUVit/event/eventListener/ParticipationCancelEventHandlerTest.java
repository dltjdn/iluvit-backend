package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.iluvit.alarms.Alarm;
import FIS.iLUVit.domain.iluvit.alarms.ConvertedToParticipateAlarm;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.event.ParticipationCancelEvent;
import FIS.iLUVit.repository.iluvit.ParticipationRepository;
import FIS.iLUVit.repository.iluvit.WaitingRepository;
import FIS.iLUVit.service.AlarmUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static FIS.iLUVit.Creator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationCancelEventHandlerTest {

    @Mock
    ParticipationRepository participationRepository;
    @Mock
    WaitingRepository waitingRepository;

    @InjectMocks
    ParticipationCancelEventHandler target;

    @Nested
    @DisplayName("설명회 취소 대기자를 설명회 등록")
    class doParticipate{

        @Test
        public void 설명회_대기자_없음_오류() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            ParticipationCancelEvent event = new ParticipationCancelEvent(presentation, ptDate);

            doReturn(null)
                    .when(waitingRepository).findMinWaitingOrder(ptDate);
            //when
            target.changeWaitingToParticipation(event);

            //then
            Assertions.assertThat(ptDate.getWaitingCnt()).isEqualTo(0);
        }

        @Test
        public void 설명회_대기자_있음() throws Exception {
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                //given
                Center center = createCenter("test", true, true, null);
                Presentation presentation = createValidPresentation(center);
                PtDate ptDate = createCanNotRegisterPtDate(presentation);
                Parent parent = createParent();
                Participation participation = createCancelParticipation(ptDate, parent);
                Waiting waiting1 = createWaiting(ptDate, parent, 5);
                Waiting waiting2 = createWaiting(ptDate, parent, 2);
                Waiting waiting3 = createWaiting(ptDate, parent, 3);

                ParticipationCancelEvent event = new ParticipationCancelEvent(presentation, ptDate);

                doReturn(waiting2)
                        .when(waitingRepository).findMinWaitingOrder(ptDate);
                doReturn(null)
                        .when(participationRepository).save(any(Participation.class));
                doNothing()
                        .when(waitingRepository).delete(waiting2);
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회 전환됨");

                ConvertedToParticipateAlarm alarm = new ConvertedToParticipateAlarm(parent, presentation, center);
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                        .thenReturn(new AlarmEvent(alarm));

                //when
                target.changeWaitingToParticipation(event);

                //then
                Assertions.assertThat(ptDate.getWaitingCnt()).isEqualTo(0);
                verify(waitingRepository, times(1)).delete(waiting2);
            }
        }
    }

}