package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.SignupTeacherRequest;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Kindergarten;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

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

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private Center center1;
    private Center center2;
    private Teacher teacher1;
    private Teacher teacher2;
    private Teacher teacher3;
    private Teacher teacher4;

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
        teacher3 = Creator.createTeacher(5L, "teacher1", center2);
        teacher4 = Creator.createTeacher(6L, "teacher1", null);
    }

    @Test
    public void 교사회원가입_실패_로그인아이디길이() throws Exception {
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
        String url = "/signup/teacher";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 교사회원가입_실패_존재하지않는시설로등록() throws Exception {
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
                .centerId(-1L)
                .build();
        String url = "/signup/teacher";
        SignupErrorResult errorResult = SignupErrorResult.NOT_EXIST_CENTER;
        doThrow(new SignupException(errorResult))
                .when(centerRepository)
                .findByIdWithTeacher(request.getCenterId());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage())
                        )
                ));
    }
}
