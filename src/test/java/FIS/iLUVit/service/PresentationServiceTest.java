package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.alarms.Alarm;
import FIS.iLUVit.domain.iluvit.alarms.PresentationFullAlarm;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.Status;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto;
import FIS.iLUVit.repository.common.CenterRepository;
import FIS.iLUVit.repository.iluvit.PresentationRepository;
import FIS.iLUVit.repository.iluvit.PtDateRepository;
import FIS.iLUVit.repository.iluvit.UserRepository;
import FIS.iLUVit.repository.iluvit.WaitingRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static FIS.iLUVit.Creator.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PresentationServiceTest {

    @Mock
    private PresentationRepository presentationRepository;
    @Mock
    private PtDateRepository ptDateRepository;
    @Mock
    private CenterRepository centerRepository;
    @Spy
    private ImageServiceImpl imageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WaitingRepository waitingRepository;
    @InjectMocks
    private PresentationService target;

    Center center;
    Center center2;
    Presentation presentation;
    PtDate ptDate1;
    PtDate ptDate2;
    MockMultipartFile mockMultipartFile;

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }

    @BeforeEach
    void init() throws IOException {

        center = Center.builder()
                .id(1L)
                .name("test 유치원")
                .build();

        presentation = Presentation.builder()
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
                .presentation(presentation)
                .build();

        Parent parent = Parent.builder()
                .auth(Auth.PARENT)
                .name("test")
                .build();

        Participation participation = Participation.builder()
                .ptDate(ptDate1)
                .parent(parent)
                .status(Status.JOINED)
                .build();

    }

    @Test
    void 설명회_수정하기() {
        //given
//        String centerDir = imageService.getCenterDir(1L);
        //when

        //then
    }

    @Test
    void 설명회_수정_잘못된_설명회_아이디로_접근시_오류(){
        //given

        //when

        //then
    }

    @Test
    void 설명회_수정_승인안된_교사가_수정시_오류(){
        //given

        //when

        //then
    }

    @Nested
    @DisplayName("셜명회 자세히 보기")
    class 설명회자세히보기{

        @Test
        @DisplayName("[success] 설명회 보기 성공 로그인 X")
        public void 설명회자세히보기로그인X() throws Exception {
            //given
            List<PresentationWithPtDatesDto> data = new ArrayList<>();
            PresentationWithPtDatesDto data1 = new PresentationWithPtDatesDto(1L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data2 = new PresentationWithPtDatesDto(1L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data3 = new PresentationWithPtDatesDto(2L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data4 = new PresentationWithPtDatesDto(2L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            data.add(data1);
            data.add(data2);
            data.add(data3);
            data.add(data4);
            Mockito.doReturn(data)
                    .when(presentationRepository).findByCenterAndDateWithPtDates(1L, LocalDate.now());

            //when
            List<PresentationDetailResponse> result = target.findPresentationByCenterIdAndDate(1L, null);

            //then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(1).getPtDateDtos().size()).isEqualTo(2);
            assertThat(result.get(0).getPlace()).isEqualTo("test place");
        }

        @Test
        @DisplayName("[success] 설명회 보기 성공 로그인 O")
        public void 설명회자세히보기로그인O() throws Exception {
            //given
            List<PresentationWithPtDatesDto> data = new ArrayList<>();
            PresentationWithPtDatesDto data1 = new PresentationWithPtDatesDto(1L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data2 = new PresentationWithPtDatesDto(1L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data3 = new PresentationWithPtDatesDto(2L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            PresentationWithPtDatesDto data4 = new PresentationWithPtDatesDto(2L, LocalDate.now(), LocalDate.now(), "test place", "content", 0, 0, "image", 1L, LocalDate.now(), "time", 0, 0, 0);
            data.add(data1);
            data.add(data2);
            data.add(data3);
            data.add(data4);
            Mockito.doReturn(data)
                    .when(presentationRepository).findByCenterAndDateWithPtDates(1L, LocalDate.now(), 1L);

            //when
            List<PresentationDetailResponse> result = target.findPresentationByCenterIdAndDate(1L, 1L);

            //then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(1).getPtDateDtos().size()).isEqualTo(2);
            assertThat(result.get(0).getPlace()).isEqualTo("test place");
        }
    }

    @Nested
    @DisplayName("설명회 저장")
    class 설명회저장{

        PtDateDetailRequest ptDateRequest1;
        PtDateDetailRequest ptDateRequest2;
        PtDateDetailRequest ptDateRequest3;
        List<PtDateDetailRequest> dtoList = new ArrayList<>();
        PresentationDetailRequest request;
        MultipartFile multipartFile;
        List<MultipartFile> multipartFileList = new ArrayList<>();


        @BeforeEach
        void init() throws IOException {
             ptDateRequest1 = new PtDateDetailRequest(LocalDate.now(), "test time", 10);
             ptDateRequest2 = new PtDateDetailRequest(LocalDate.now(), "test time", 10);
             ptDateRequest3 = new PtDateDetailRequest(LocalDate.now(), "test time", 10);
             List<PtDateDetailRequest> dtoList = new ArrayList<>();
             dtoList.add(ptDateRequest1);
             dtoList.add(ptDateRequest2);
             dtoList.add(ptDateRequest3);
             request = new PresentationDetailRequest(1L, LocalDate.now(), LocalDate.now(), "test place", "test content", dtoList);
            String name = "162693895955046828.png";
            Path path1 = Paths.get(new File("").getAbsolutePath() + '/' + name);
            byte[] content = Files.readAllBytes(path1);
            multipartFile = new MockMultipartFile(name, name, "image", content);
            multipartFileList.add(multipartFile);
            multipartFileList.add(multipartFile);
        }

        @Test
        @DisplayName("[error] 설명회 저장시 존재하지 않는 선생님")
        public void 존재하지않는선생() throws Exception {
            //given
            Mockito.doReturn(Optional.empty())
                    .when(userRepository).findTeacherById(1L);
            //when
            UserException result = assertThrows(UserException.class, () -> {
                target.saveInfoWithPtDate(request,  1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_EXIST);
        }

        @Test
        @DisplayName("[error] 설명회 저장 권한 없음")
        public void 설명회작성권한없는경우() throws Exception {
            //given
            Teacher teacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.WAITING);
            Mockito.doReturn(Optional.of(teacher))
                    .when(userRepository).findTeacherById(1L);
            //when
            CenterException result = assertThrows(CenterException.class, () -> {
                target.saveInfoWithPtDate(request,1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(CenterErrorResult.AUTHENTICATION_FAILED);
        }

        @Test
        @DisplayName("[error] 이미 유효한 설명회 존재")
        public void 유효한설명회존재() throws Exception {
            //given
            Teacher teacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.ACCEPT);
            Mockito.doReturn(Optional.of(teacher))
                    .when(userRepository).findTeacherById(1L);
            Mockito.doReturn(presentation)
                    .when(presentationRepository).findByCenterIdAndDate(request.getCenterId(), LocalDate.now());

            //when
            PresentationException result = assertThrows(PresentationException.class, () -> {
                target.saveInfoWithPtDate(request, 1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(PresentationErrorResult.ALREADY_PRESENTATION_EXIST);
        }

        @Test
        @DisplayName("[success] 설명회 저장 성공")
        public void 설명회저장성공() throws Exception {
            //given
            Teacher teacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.ACCEPT);
            Mockito.doReturn(Optional.of(teacher))
                    .when(userRepository).findTeacherById(1L);
            Mockito.doReturn(null)
                    .when(presentationRepository).findByCenterIdAndDate(request.getCenterId(), LocalDate.now());
            Mockito.doReturn("")
                    .when(imageService).saveInfoImages(anyList(), any(Presentation.class));
            Mockito.doReturn(center)
                    .when(centerRepository).getById(center.getId());

            //when
            Presentation result = target.saveInfoWithPtDate(request, 1L);

            //then
            assertThat(result.getCenter().getId()).isEqualTo(1L);
            assertThat(result.getPtDates().size()).isEqualTo(3);
        }

    }

    @Nested
    @DisplayName("설명회 수정")
    class 설명회수정{
        PtDateDto dto1;
        PtDateDto dto2;
        PtDateDto dto3;
        PtDateDto dto4;
        PresentationRequest request;
        Presentation presentation;
        PtDate ptDate1;
        PtDate ptDate2;
        PtDate ptDate3;
        Teacher acceptTeacher;
        Teacher waitingTeacher;
        List<Waiting> waiting;
        @BeforeEach
        void init(){
            dto1 = new PtDateDto(null, LocalDate.now(), "test time", 10);
            dto2 = new PtDateDto(1L, LocalDate.now(), "test time", 10);
            dto3 = new PtDateDto(null, LocalDate.now(), "test time", 10);
            dto4 = new PtDateDto(3L, LocalDate.now(), "test time", 10);
            List<PtDateDto> dtos = new ArrayList<>();
            dtos.add(dto1);
            dtos.add(dto2);
            dtos.add(dto3);
            dtos.add(dto4);
            request = new PresentationRequest(1L, LocalDate.now(), LocalDate.now(), "장소", "콘텐츠", dtos);
            presentation = createValidPresentation(center);
            ptDate1 = createCanRegisterPtDate(1L, presentation);
            ptDate2 = createCanNotRegisterPtDate(2L, presentation);
            ptDate3 = createCanNotRegisterPtDate(3L, presentation);
            presentation.getPtDates().add(ptDate1);
            presentation.getPtDates().add(ptDate2);
            presentation.getPtDates().add(ptDate3);
            acceptTeacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.ACCEPT);
            waitingTeacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.WAITING);
            waiting = List.of(createWaiting(1L, ptDate2, createParent(1L), 1));
        }

        @Test
        @DisplayName("[error] 올바르지 않은 설명회 아이디")
        public void 올바르지않은설명회아이디() throws Exception {
            //given
            Mockito.doReturn(Optional.empty())
                    .when(presentationRepository).findByIdAndJoinPtDate(request.getPresentationId());
            //when
            PresentationException result = assertThrows(PresentationException.class, () -> {
                target.modifyInfoWithPtDate(request, 1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(PresentationErrorResult.NO_RESULT);

        }

        @Test
        @DisplayName("[error] 존재하지않는 선생")
        public void 존재하지않는선생() throws Exception {
            //given
            Mockito.doReturn(Optional.of(presentation))
                    .when(presentationRepository).findByIdAndJoinPtDate(request.getPresentationId());
            Mockito.doReturn(Optional.empty())
                    .when(userRepository).findTeacherById(1L);

            //when
            UserException result = assertThrows(UserException.class, () -> {
                target.modifyInfoWithPtDate(request, 1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_EXIST);

        }

        @Test
        @DisplayName("[error] 수정 권한 없는 선생")
        public void 권한없는선생() throws Exception {
            //given
            Mockito.doReturn(Optional.of(presentation))
                    .when(presentationRepository).findByIdAndJoinPtDate(request.getPresentationId());
            Mockito.doReturn(Optional.of(waitingTeacher))
                    .when(userRepository).findTeacherById(1L);

            //when
            CenterException result = assertThrows(CenterException.class, () -> {
                target.modifyInfoWithPtDate(request, 1L);
            });

            //then
            assertThat(result.getErrorResult()).isEqualTo(CenterErrorResult.AUTHENTICATION_FAILED);

        }

        @Test
        @DisplayName("[error] 설명회 회차 삭제 불가능")
        public void 설명회회차삭제실패() throws Exception {
            //given
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                List<Long> collect = waiting.stream().map(Waiting::getId).collect(toList());
                Mockito.doReturn(Optional.of(presentation))
                        .when(presentationRepository).findByIdAndJoinPtDate(request.getPresentationId());
                Mockito.doReturn(Optional.of(acceptTeacher))
                        .when(userRepository).findTeacherById(1L);
                Mockito.doReturn(null)
                        .when(ptDateRepository).save(any(PtDate.class));
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회가 가득 찼습니다");

                PresentationFullAlarm presentationFullAlarm = new PresentationFullAlarm(createParent(1L), presentation, center);
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                        .thenReturn(new AlarmEvent(presentationFullAlarm));
                //when
                PresentationException result = assertThrows(PresentationException.class,
                        () -> target.modifyInfoWithPtDate(request, 1L));

                //then
                verify(ptDateRepository, times(2)).save(any(PtDate.class));
                assertThat(result.getErrorResult()).isEqualTo(PresentationErrorResult.DELETE_FAIL_HAS_PARTICIPANT);
            }
        }

        @Test
        @DisplayName("[success] 설명회 수정 성공")
        public void 설명회수정성공() throws Exception {
            //given
            dto1 = new PtDateDto(null, LocalDate.now(), "test time", 10);
            dto2 = new PtDateDto(1L, LocalDate.now(), "test time", 10);
            dto3 = new PtDateDto(null, LocalDate.now(), "test time", 10);
            dto4 = new PtDateDto(3L, LocalDate.now(), "test time", 10);
            List<PtDateDto> dtos = new ArrayList<>();
            dtos.add(dto1);
            dtos.add(dto2);
            dtos.add(dto3);
            dtos.add(dto4);
            request = new PresentationRequest(1L, LocalDate.now(), LocalDate.now(), "장소", "콘텐츠", dtos);
            request = new PresentationRequest(1L, LocalDate.now(), LocalDate.now(), "장소", "콘텐츠", dtos);
            presentation = createValidPresentation(center);
            ptDate1 = createCanRegisterPtDate(1L, presentation);
            ptDate2 = createNoParticipantsPtDate(2L, presentation);
            ptDate3 = createCanNotRegisterPtDate(3L, presentation);
            presentation.getPtDates().add(ptDate1);
            presentation.getPtDates().add(ptDate2);
            presentation.getPtDates().add(ptDate3);
            acceptTeacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.ACCEPT);
            waitingTeacher = createTeacher(1L, center, Auth.DIRECTOR, Approval.WAITING);
            waiting = List.of(createWaiting(1L, ptDate2, createParent(1L), 1));


            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                List<Long> collect = waiting.stream().map(Waiting::getId).collect(toList());
                Mockito.doReturn(Optional.of(presentation))
                        .when(presentationRepository).findByIdAndJoinPtDate(request.getPresentationId());
                Mockito.doReturn(Optional.of(acceptTeacher))
                        .when(userRepository).findTeacherById(1L);
                Mockito.doReturn(null)
                        .when(ptDateRepository).save(any(PtDate.class));
                Mockito.doReturn(waiting)
                        .when(waitingRepository).findWaitingsByPtDateAndOrderNum(ptDate3, 7);
                Mockito.doNothing()
                        .when(waitingRepository).deleteAllByIdInBatch(collect);
                Mockito.doNothing()
                        .when(waitingRepository).updateWaitingOrderForPtDateChange(7, ptDate3);
                Mockito.doNothing()
                        .when(ptDateRepository).deletePtDateByIds(Set.of(2L));
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회가 가득 찼습니다");
                PresentationFullAlarm presentationFullAlarm = new PresentationFullAlarm(createParent(1L), presentation, center);
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                        .thenReturn(new AlarmEvent(presentationFullAlarm));
                //when
                Presentation presentation = target.modifyInfoWithPtDate(request, 1L);

                //then
                verify(ptDateRepository, times(2)).save(any(PtDate.class));
                verify(waitingRepository, times(1)).findWaitingsByPtDateAndOrderNum(ptDate3, 7);
                verify(waitingRepository, times(0)).findWaitingsByPtDateAndOrderNum(ptDate2, 7);
                verify(waitingRepository, times(1)).deleteAllByIdInBatch(collect);
                verify(waitingRepository, times(1)).updateWaitingOrderForPtDateChange(7, ptDate3);
                verify(waitingRepository, times(0)).updateWaitingOrderForPtDateChange(7, ptDate2);
                verify(ptDateRepository, times(1)).deletePtDateByIds(Set.of(2L));
                assertThat(presentation.getPtDates().size()).isEqualTo(4);
            }
        }
    }
}