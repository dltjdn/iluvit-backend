package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.SignupTeacherRequest;
import FIS.iLUVit.controller.dto.TeacherDetailResponse;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static FIS.iLUVit.service.createmethod.CreateTest.createBoard;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @InjectMocks
    private TeacherService target;

    @Mock
    private AuthNumberService authNumberService;
    @Mock
    private UserService userService;
    @Mock
    private CenterRepository centerRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private AuthNumberRepository authNumberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private ImageService imageService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private Center center1;
    private Center center2;
    private Teacher teacher1;
    private Teacher teacher2;
    private Teacher teacher3;
    private Teacher teacher4;
    private Board board1;
    private Board board2;
    private Board board3;
    private Board board4;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        center1 = CreateTest.createCenter(1L, "center1");
        center2 = CreateTest.createCenter(2L, "center2");
        teacher1 = Creator.createTeacher(3L, "teacher1", center1);
        teacher2 = Creator.createTeacher(4L, "teacher2", center1);
        teacher3 = Creator.createTeacher(5L, "teacher1", center1);
        teacher4 = Creator.createTeacher(6L, "teacher1", null);
        board1 = createBoard(3L, "자유게시판", BoardKind.NORMAL, null, true);
        board2 = createBoard(4L, "맛집게시판", BoardKind.NORMAL, null, true);
        board3 = createBoard(5L, "공지게시판", BoardKind.NORMAL, center1, true);
        board4 = createBoard(6L, "자유게시판", BoardKind.NORMAL, center1, true);
        center1.getTeachers().add(teacher1);
        center1.getTeachers().add(teacher2);
        center1.getTeachers().add(teacher3);
        center1.getBoards().add(board3);
        center1.getBoards().add(board4);
    }


    @Test
    public void 교사회원가입_실패_없는시설로등록() {
        // given
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
                () -> target.signup(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_EXIST_CENTER);
    }

    @Test
    public void 교사회원가입_성공_센터를선택한경우() {
        // given
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
        try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
            alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                    .thenReturn("설명회가 가득 찼습니다");
            AlarmEvent alarmEvent = new AlarmEvent(new CenterApprovalReceivedAlarm(Teacher.builder().build()));
            alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(Teacher.builder().build())))
                    .thenReturn(alarmEvent);
            doReturn("hashedPwd")
                    .when(userService)
                    .signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
            doReturn(Optional.of(center1))
                    .when(centerRepository)
                    .findByIdWithTeacher(request.getCenterId());
            doReturn(Arrays.asList(board1, board2))
                    .when(boardRepository)
                    .findDefaultByModu();
            // when
            Teacher result = target.signup(request);
            // then
            assertThat(result.getLoginId()).isEqualTo(request.getLoginId());
            assertThat(result.getCenter().getId()).isEqualTo(request.getCenterId());
            // verify
            verify(bookmarkRepository, times(2)).save(any(Bookmark.class));
            verify(centerRepository, times(1)).findByIdWithTeacher(request.getCenterId());
            verify(scrapRepository, times(1)).save(any(Scrap.class));
            verify(authNumberRepository, times(1)).deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
        }
    }

    @Test
    public void 교사회원가입_성공_시설선택X() {
        // given
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
                .signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
        doReturn(new ArrayList<Board>())
                .when(boardRepository)
                .findDefaultByModu();
        // when
        Teacher result = target.signup(request);
        // then
        assertThat(result.getLoginId()).isEqualTo(request.getLoginId());
        assertThat(result.getCenter()).isNull();
        // verify
        verify(centerRepository, times(0)).findByIdWithTeacher(request.getCenterId());
        verify(bookmarkRepository, times(0)).save(any(Bookmark.class));
        verify(scrapRepository, times(1)).save(any(Scrap.class));
        verify(authNumberRepository, times(1)).deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
    }

    @Test
    public void 교사프로필조회_성공() throws IOException {
        // given
        doReturn(Optional.of(teacher1))
                .when(teacherRepository)
                .findById(teacher1.getId());
        TeacherDetailResponse response = new TeacherDetailResponse(teacher1);
        doReturn("imagePath")
                .when(imageService)
                .getProfileImage(teacher1);
        // when
        TeacherDetailResponse result = target.findDetail(teacher1.getId());
        // then
        assertThat(result.getNickname()).isEqualTo(response.getNickname());
        assertThat(result.getProfileImg()).isEqualTo("imagePath");
    }


}
