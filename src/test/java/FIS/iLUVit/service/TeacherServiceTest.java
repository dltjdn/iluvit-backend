package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.service.createmethod.CreateTest.createBoard;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @InjectMocks
    private TeacherService target;

    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private CenterRepository centerRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;
    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private MapService mapService;

    private Center center1;
    private Center center2;
    private Teacher teacher1;
    private Teacher teacher2;
    private Teacher teacher3;
    private Teacher teacher4;
    private Teacher teacher5;
    private Board board1;
    private Board board2;
    private Board board3;
    private Board board4;
    private MockMultipartFile multipartFile;
    @BeforeEach
    public void init() throws IOException {
        center1 = CreateTest.createCenter(1L, "center1");
        center2 = CreateTest.createCenter(2L, "center2");
        teacher1 = Creator.createTeacher(3L, "teacher1", center1, Auth.DIRECTOR, Approval.ACCEPT);
        teacher2 = Creator.createTeacher(4L, "teacher2", center1, Auth.TEACHER, Approval.ACCEPT);
        teacher3 = Creator.createTeacher(5L, "teacher3", center1, Auth.TEACHER, Approval.WAITING);
        teacher4 = Creator.createTeacher(6L, "teacher4", null, Auth.TEACHER, null);
        teacher5 = Creator.createTeacher(7L, "teacher3", center1, Auth.TEACHER, Approval.WAITING);
        board1 = createBoard(8L, "자유게시판", BoardKind.NORMAL, null, true);
        board2 = createBoard(9L, "맛집게시판", BoardKind.NORMAL, null, true);
        board3 = createBoard(10L, "공지게시판", BoardKind.NORMAL, center1, true);
        board4 = createBoard(11L, "자유게시판", BoardKind.NORMAL, center1, true);
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
    }


    @Test
    public void 교사회원가입_실패_없는시설로등록() {
        // given
        center1.getTeachers().add(teacher1);
        center1.getTeachers().add(teacher2);
        center1.getTeachers().add(teacher3);
        center1.getTeachers().add(teacher5);
        SignupTeacherRequest request = SignupTeacherRequest.builder()
                .loginId("log")
                .password("password")
                .passwordCheck("password")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .centerId(center1.getId())
                .build();
        doReturn(Optional.empty())
                .when(centerRepository)
                .findByIdWithTeacher(request.getCenterId());
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.signupTeacher(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_EXIST_CENTER);
    }

    @Test
    public void 교사회원가입_성공_시설선택O() {
        try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            SignupTeacherRequest request = SignupTeacherRequest.builder()
                    .loginId("loginId")
                    .password("password")
                    .passwordCheck("password")
                    .phoneNum("phoneNum")
                    .nickname("nickName")
                    .name("name")
                    .emailAddress("asd@asd")
                    .address("address")
                    .detailAddress("detailAddress")
                    .centerId(center1.getId())
                    .build();
            alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                    .thenReturn("설명회가 가득 찼습니다");
            AlarmEvent alarmEvent = new AlarmEvent(new CenterApprovalReceivedAlarm(Teacher.builder().build(), Auth.TEACHER, Center.builder().build()));
            alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(Teacher.builder().build(), Auth.TEACHER, Center.builder().build())))
                    .thenReturn(alarmEvent);
            doReturn("hashedPwd")
                    .when(userService)
                    .hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
            doReturn(Optional.of(center1))
                    .when(centerRepository)
                    .findByIdWithTeacher(request.getCenterId());
            doReturn(Arrays.asList(board1, board2))
                    .when(boardRepository)
                    .findDefaultByModu();
            //when
            Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                    .when(mapService).convertAddressToLocation(anyString());
            Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                    .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
            // when

            Teacher result = target.signupTeacher(request);
            // then
            assertThat(result.getLoginId()).isEqualTo(request.getLoginId());
            assertThat(result.getCenter().getId()).isEqualTo(request.getCenterId());
            // verify
            verify(boardBookmarkRepository, times(2)).save(any(Bookmark.class));
            verify(centerRepository, times(1)).findByIdWithTeacher(request.getCenterId());
            verify(scrapRepository, times(1)).save(any(Scrap.class));
            verify(authRepository, times(1)).deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
        }
    }

    @Test
    public void 교사회원가입_성공_시설선택X() {
        // given
        center1.getTeachers().add(teacher1);
        center1.getTeachers().add(teacher2);
        center1.getTeachers().add(teacher3);
        center1.getTeachers().add(teacher5);
        SignupTeacherRequest request = SignupTeacherRequest.builder()
                .loginId("loginId")
                .password("password")
                .passwordCheck("password")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .centerId(null)
                .build();
        doReturn("hashedPwd")
                .when(userService)
                .hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
        doReturn(new ArrayList<Board>())
                .when(boardRepository)
                .findDefaultByModu();
        //when
        Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                .when(mapService).convertAddressToLocation(anyString());
        Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
        // when
        Teacher result = target.signupTeacher(request);
        // then
        assertThat(result.getLoginId()).isEqualTo(request.getLoginId());
        assertThat(result.getCenter()).isNull();
        // verify
        verify(centerRepository, times(0)).findByIdWithTeacher(request.getCenterId());
        verify(boardBookmarkRepository, times(0)).save(any(Bookmark.class));
        verify(scrapRepository, times(1)).save(any(Scrap.class));
        verify(authRepository, times(1)).deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
    }

    @Test
    public void 교사프로필조회_성공() throws IOException {
        // given
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        TeacherDetailResponse response = new TeacherDetailResponse(teacher1,teacher1.getProfileImagePath());
        doReturn("imagePath")
                .when(imageService)
                .getProfileImage(teacher1);
        // when
        TeacherDetailResponse result = target.findTeacherDetails(teacher1.getId());
        // then
        assertThat(result.getNickname()).isEqualTo(response.getNickname());
        assertThat(result.getProfileImg()).isEqualTo("imagePath");
    }

    @Test
    public void 교사프로필수정_실패_닉네임중복() {
        // given
        TeacherDetailRequest request = TeacherDetailRequest.builder()
                .nickname("중복닉네임")
                .build();
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        doReturn(Optional.of(Teacher.builder().build()))
                .when(teacherRepository)
                .findByNickName("중복닉네임");
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.saveTeacherDetailsChanges(teacher1.getId(), request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.DUPLICATED_NICKNAME);
    }

    @Test
    public void 교사프로필수정_실패_핸드폰변경시미인증() {
        // given
        TeacherDetailRequest request = TeacherDetailRequest.builder()
                .name(teacher1.getName())
                .nickname(teacher1.getNickName())
                .changePhoneNum(true)
                .phoneNum("newPhoneNum")
                .emailAddress(teacher1.getEmailAddress())
                .address(teacher1.getAddress())
                .detailAddress(teacher1.getDetailAddress())
                .profileImg(multipartFile)
                .build();
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        AuthNumberErrorResult error = AuthNumberErrorResult.NOT_AUTHENTICATION;
        doThrow(new AuthNumberException(error))
                .when(authService)
                .validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.saveTeacherDetailsChanges(teacher1.getId(), request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_AUTHENTICATION);
    }

    @Test
    public void 교사프로필수정_성공_핸드폰포함() throws IOException {
        // given
        TeacherDetailRequest request = TeacherDetailRequest.builder()
                .name(teacher1.getName())
                .nickname(teacher1.getNickName())
                .changePhoneNum(true)
                .phoneNum("newPhoneNum")
                .emailAddress(teacher1.getEmailAddress())
                .address(teacher1.getAddress())
                .detailAddress(teacher1.getDetailAddress())
                .profileImg(multipartFile)
                .build();
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                .when(mapService).convertAddressToLocation(null);
        Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
        // when
        TeacherDetailResponse response = target.saveTeacherDetailsChanges(teacher1.getId(), request);
        // then
        assertThat(response.getPhoneNumber()).isEqualTo("newPhoneNum");
    }

    @Test
    public void 교사프로필수정_성공_핸드폰미포함() throws IOException {
        // given
        TeacherDetailRequest request = TeacherDetailRequest.builder()
                .name("updatedName")
                .nickname(teacher1.getNickName())
                .changePhoneNum(false)
                .phoneNum("newPhoneNum")
                .emailAddress(teacher1.getEmailAddress())
                .address(teacher1.getAddress())
                .detailAddress(teacher1.getDetailAddress())
                .profileImg(multipartFile)
                .build();
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                .when(mapService).convertAddressToLocation(null);
        Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
        // when
        TeacherDetailResponse result = target.saveTeacherDetailsChanges(teacher1.getId(), request);
        // then
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getPhoneNumber()).isNotEqualTo(request.getPhoneNum());
    }

    @Nested
    @DisplayName("시설에 등록신청")
    class AssignCenter{

        @Test
        @DisplayName("[error] 이미 시설에 등록되있는경우")
        public void 등록된시설있음() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findByIdAndNotAssign(teacher1.getId());
            // when
            SignupException result = assertThrows(SignupException.class,
                    () -> target.acceptTeacherRegistration(teacher1.getId(), center2.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.ALREADY_BELONG_CENTER);
        }

        @Test
        @DisplayName("[success] 시설로의 등록신청")
        public void 시설등록신청_성공() {
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                // given
                doReturn(Optional.of(teacher4))
                        .when(teacherRepository)
                        .findByIdAndNotAssign(teacher4.getId());
                doReturn(center1)
                        .when(centerRepository)
                        .getById(center1.getId());
                doReturn(List.of(teacher1))
                        .when(teacherRepository)
                        .findDirectorByCenter(center1.getId());
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회가 가득 찼습니다");
                AlarmEvent alarmEvent = new AlarmEvent(new CenterApprovalAcceptedAlarm(Parent.builder().build(), Center.builder().build()));
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(teacher1, Auth.TEACHER, teacher1.getCenter())))
                        .thenReturn(alarmEvent);
                // when
                Teacher result = target.acceptTeacherRegistration(teacher4.getId(), center1.getId());
                // then
                assertThat(result.getCenter().getId()).isEqualTo(center1.getId());
                assertThat(result.getApproval()).isEqualTo(Approval.WAITING);
                alarmUtils.verify(() -> AlarmUtils.publishAlarmEvent(any()), times(1));
            }
        }
    }

    @Nested
    @DisplayName("시설 스스로 탈주하기")
    class escapeCenter{
        @Test
        @DisplayName("[error] 사용자가 해당시설에 속해있지않음")
        public void 해당시설에속해있지않음() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findByIdWithCenterWithTeacher(any());
            // when
            SignupException result = assertThrows(SignupException.class,
                    () -> target.resignCenterForTeacher(teacher2.getId()));

            // then
            assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_BELONG_CENTER);
        }

        @Test
        @DisplayName("[error] 일반교사가있는 시설에 마지막 원장의 탈주")
        public void 마지막원장탈주실패() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findByIdWithCenterWithTeacher(any());
            // when
            SignupException result = assertThrows(SignupException.class,
                    () -> target.resignCenterForTeacher(teacher1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.HAVE_TO_MANDATE);
        }

        @Test
        @DisplayName("[success] 시설탈주 성공")
        public void 시설탈주성공() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher2))
                    .when(teacherRepository)
                    .findByIdWithCenterWithTeacher(any());
            doReturn(List.of())
                    .when(boardRepository)
                    .findByCenter(teacher2.getCenter().getId());
            // when
            Teacher result = target.resignCenterForTeacher(teacher2.getId());
            // then
            assertThat(result.getId()).isEqualTo(teacher2.getId());
            assertThat(result.getCenter()).isNull();
            assertThat(result.getAuth()).isEqualTo(Auth.TEACHER);
        }
    }

    @Nested
    @DisplayName("교사관리 페이지에 필요한 교사들 정보 조회")
    class findTeacherApprovalList{
        @Test
        @DisplayName("[error] 사용자가 원장이 아닌경우")
        public void 사용자가원장이아닌경우() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.findTeacherApprovalList(teacher2.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }

        @Test
        @DisplayName("[success] 정상적인요청")
        public void 정상적인요청() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher1.getId());
            // when
            List<TeacherInfoForAdminDto> result = target.findTeacherApprovalList(teacher1.getId());
            // then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(3);
        }

    }


    @Nested
    @DisplayName("교사승인")
    class acceptTeacher{
        @Test
        @DisplayName("[error] 원장이 아닌 사용자의 요청")
        public void 원장이아닌사용자의요청() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher2.getId());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.acceptTeacherRegistration(teacher2.getId(), teacher3.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }

        @Test
        @DisplayName("[error] 올바르지 않은 교사의 승인")
        public void 요청하지않은교사의승인() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher1.getId());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.acceptTeacherRegistration(teacher1.getId(), teacher4.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 정상적인 요청")
        public void 정상적인요청() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher1.getId());
            doReturn(List.of(board3, board4))
                    .when(boardRepository)
                    .findDefaultByCenter(teacher1.getCenter().getId());
            // when
            Teacher result = target.acceptTeacherRegistration(teacher1.getId(), teacher3.getId());
            // then
            assertThat(result.getId()).isEqualTo(teacher3.getId());
            assertThat(result.getApproval()).isEqualTo(Approval.ACCEPT);
            verify(boardRepository, times(1)).findDefaultByCenter(teacher1.getCenter().getId());
            verify(boardBookmarkRepository, times(2)).save(any());
        }
    }

    @Nested
    @DisplayName("교사 승인신청 삭제/거절")
    class fireTeacher{
        @Test
        @DisplayName("[error] 원장이 아닌 사용자의 요청")
        public void 원장이아닌사용자의요청() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findDirectorById(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.rejectTeacherRegistration(teacher1.getId(), teacher2.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }

        @Test
        @DisplayName("[error] 올바르지않은 교사 삭제")
        public void 올바르지않은교사() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorById(teacher1.getId());
            doReturn(Optional.of(teacher4))
                    .when(teacherRepository)
                    .findById(teacher4.getId());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.rejectTeacherRegistration(teacher1.getId(), teacher4.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[error] 존재하지않는 교사 아이디")
        public void 존재하지않는교사아이디() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorById(teacher1.getId());
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findById(teacher4.getId());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.rejectTeacherRegistration(teacher1.getId(), teacher4.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 교사 삭제 성공")
        public void 교사삭제성공() {
            // given
            Bookmark bookmark1 = Bookmark.builder()
                    .board(board3)
                    .user(teacher2)
                    .build();
            Bookmark bookmark2 = Bookmark.builder()
                    .board(board4)
                    .user(teacher2)
                    .build();
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorById(teacher1.getId());
            doReturn(Optional.of(teacher2))
                    .when(teacherRepository)
                    .findById(teacher2.getId());
            doReturn(List.of(board3, board4))
                    .when(boardRepository)
                    .findByCenter(any());
            // when
            Teacher result = target.rejectTeacherRegistration(teacher1.getId(), teacher2.getId());
            // then
            assertThat(result.getCenter()).isNull();
            verify(boardRepository, times(1)).findByCenter(teacher1.getCenter().getId());
            verify(boardBookmarkRepository, times(1)).deleteAllByBoardAndUser(any(), any());
        }
    }

    @Nested
    @DisplayName("원장권한부여")
    class mandateTeacher{

        @Test
        @DisplayName("[error] 원장이 아닌 경우")
        public void 원장아님() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.mandateTeacher(teacher2.getId(), teacher3.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);

        }
        @Test
        @DisplayName("[error] 시설에 속해있지 않은 교사")
        public void 시설에속해있지않은교사() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.mandateTeacher(teacher1.getId(), teacher4.getId()));
            // then

        }

        @Test
        @DisplayName("[error] 승인받지 않은 교사")
        public void 승인받지않은교사() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.mandateTeacher(teacher1.getId(), teacher3.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 원장권한 부여 성공")
        public void 권한부여성공() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            Teacher result = target.mandateTeacher(teacher1.getId(), teacher2.getId());
            // then
            assertThat(result.getAuth()).isEqualTo(Auth.DIRECTOR);
        }
    }
    
    @Nested
    @DisplayName("원장권한 박탈")
    class demoteTeacher{
        @Test
        @DisplayName("[error] 원장이 아닌 경우")
        public void 원장아님() {
            // given
            Teacher localTeacher = Creator.createTeacher(3L, "localTeacher", center1, Auth.DIRECTOR, Approval.ACCEPT);
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            center1.getTeachers().add(localTeacher);
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.demoteTeacher(teacher2.getId(), teacher3.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }
        @Test
        @DisplayName("[error] 해당시설에 속해있지 않은 교사")
        public void 속해있지않은교사() {
            // given
            Teacher localTeacher = Creator.createTeacher(3L, "localTeacher", center1, Auth.DIRECTOR, Approval.ACCEPT);
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            center1.getTeachers().add(localTeacher);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher1.getId());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.demoteTeacher(teacher1.getId(), teacher4.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }
        @Test
        @DisplayName("[success] 원장권한 박탈 성공")
        public void 박탈성공() {
            // given
            Teacher localTeacher = Creator.createTeacher(3L, "localTeacher", center1, Auth.DIRECTOR, Approval.ACCEPT);
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            center1.getTeachers().add(teacher5);
            center1.getTeachers().add(localTeacher);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findDirectorByIdWithCenterWithTeacher(teacher1.getId());
            // when
            Teacher result = target.demoteTeacher(teacher1.getId(), localTeacher.getId());
            // then
            assertThat(result.getAuth()).isEqualTo(Auth.TEACHER);
        }
    }

}
