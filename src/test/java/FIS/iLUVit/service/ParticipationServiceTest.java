package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    /**
     * 설명회 신청의 시나리오
     * 예외 사항
     * 1. 설명회 신청기간이 지났을 경우 예외 발생
     * 2. 설명회 신청가능인원이 가득 찼을 경우
     * 3. 설명회를 이미 신청했을 경우
     */

    @Mock
    ParentRepository parentRepository;
    @Mock
    ParticipationRepository participationRepository;
    @Mock
    PtDateRepository ptDateRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ParticipationService target;


    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    Center center;
    Center center2;
    Presentation presentation1;
    Presentation presentation2;
    PtDate ptDate1;
    PtDate ptDate2;
    PtDate ptDate3;
    PtDate ptDate4;
    Participation participation;
    Parent parent;

    @BeforeEach
    public void init(){
        center = Center.builder()
                .id(1L)
                .name("test 유치원")
                .build();

        presentation1 = Presentation.builder()
                .id(1L)
                .startDate(LocalDate.of(2022, 7, 3))
                .endDate(LocalDate.of(2022, 7, 3))
                .place("테스트 장소")
                .content("테스트 설명회")
                .imgCnt(3)
                .videoCnt(1)
                .center(center)
                .build();

        presentation2 = Presentation.builder()
                .id(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .place("테스트 장소")
                .content("테스트 설명회")
                .imgCnt(3)
                .videoCnt(1)
                .center(center)
                .build();

        center2 = Center.builder()
                .id(1L)
                .address("test 주소")
                .name("test 유치원")
                .build();

        ptDate1 = PtDate.builder()
                .id(1L)
                .date(LocalDate.now())
                .time("오후 9시")
                .presentation(presentation1)
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .build();

        ptDate2 = PtDate.builder()
                .id(2L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation2)
                .build();

        ptDate3 = PtDate.builder()
                .id(3L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(2)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation2)
                .build();

        ptDate4 = PtDate.builder()
                .id(4L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(1)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation2)
                .build();

        parent = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .name("test")
                .build();

        participation = Participation.builder()
                .ptDate(ptDate2)
                .parent(parent)
                .status(Status.JOINED)
                .build();

        ptDate2.getParticipations().add(participation);


    }

    @Test
    public void 잘못된_설명회_회차_아이디로_신청() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(null))
                .when(ptDateRepository)
                .findByIdAndJoinParticipation(any(Long.class));

        //when
        PresentationException result = assertThrows(PresentationException.class,
                () -> target.register(parent.getId(), ptDate1.getId()));     // 예외가 발생 해야한다.

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST);
    }

    @Test
    public void 설명회_신청_신청기간이_지남() throws Exception {
        //given
        /**
         * 설명회 신청을 위해서는 설명회 정보가 필요하다.
         * 연관 관계 매핑 되어있음
         * service => 데이터베이스에서 정보 가져왔을 때
         *
         */
        Mockito.doReturn(Optional.of(ptDate1))
                .when(ptDateRepository)
                .findByIdAndJoinParticipation(ptDate1.getId());
        // ptdate => 섦명회, 센터

        //when
        PresentationException result = assertThrows(PresentationException.class,
                () -> target.register(parent.getId(), ptDate1.getId()));     // 예외가 발생 해야한다.

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);

    }

    @Test
    public void 설명회_신청_이미_신청한_사용자() throws Exception {
        //given
        Mockito.doReturn(Optional.of(ptDate2))
                .when(ptDateRepository)
                .findByIdAndJoinParticipation(ptDate2.getId());

        //when
        PresentationException result = assertThrows(PresentationException.class,
                () -> target.register(parent.getId(), ptDate2.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PresentationErrorResult.ALREADY_PARTICIPATED_IN);
    }

    @Test
    public void 설명회_신청_인원_초과() throws Exception {
        //given
        Mockito.doReturn(Optional.of(ptDate4))
                .when(ptDateRepository)
                .findByIdAndJoinParticipation(ptDate4.getId());
        //when

        PresentationException result = assertThrows(PresentationException.class,
                () -> target.register(parent.getId(), ptDate4.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PresentationErrorResult.PRESENTATION_OVERCAPACITY);
    }

    @Test
    public void 설명회_신청_성공() throws Exception {
        //given
        MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class);

        Participation participation1 = Participation.builder()
                .id(2L)
                .ptDate(ptDate3)
                .parent(parent)
                .status(Status.JOINED)
                .build();

        Mockito.doReturn(Optional.of(ptDate3))
                .when(ptDateRepository)
                .findByIdAndJoinParticipation(ptDate3.getId());

        Mockito.doReturn(participation1)
                .when(participationRepository)
                .save(any(Participation.class));

        Mockito.doReturn(Parent.builder().id(parent.getId()).build())
                .when(parentRepository)
                .getById(parent.getId());


        List<Teacher> teachers = new ArrayList<>();
        teachers.add(Teacher.builder().name("test").build());

        Mockito.doReturn(teachers)
                .when(userRepository)
                .findTeacherByCenter(any(Center.class));
        
        alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                .thenReturn("설명회가 가득 찼습니다");

        alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                .thenReturn(new AlarmEvent(new PresentationFullAlarm(parent, presentation1, center)));

        //when
        Long result = target.register(parent.getId(), ptDate3.getId());

        // then
        assertThat(result).isEqualTo(participation1.getId());
        // register 에서 repository
        Mockito.verify(ptDateRepository, Mockito.times(1))
                .findByIdAndJoinParticipation(ptDate3.getId());
        Mockito.verify(participationRepository, Mockito.times(1))
                .save(any(Participation.class));
        Mockito.verify(parentRepository, Mockito.times(1))
                .getById(parent.getId());

    }
}