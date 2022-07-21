package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.SignupTeacherRequest;
import FIS.iLUVit.controller.dto.TeacherDetailResponse;
import FIS.iLUVit.controller.dto.UpdateTeacherDetailRequest;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static FIS.iLUVit.Creator.createJwtToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @InjectMocks
    private TeacherController target;

    @Mock
    private TeacherService teacherService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Teacher teacher;
    private MockMultipartFile multipartFile;

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        teacher = Creator.createTeacher(1L, "teacher", null);
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
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
                .centerId(1L)
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
    public void 교사회원가입_실패_없는시설로등록() throws Exception {
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
                .centerId(1L)
                .build();
        String url = "/signup/teacher";
        SignupErrorResult error = SignupErrorResult.NOT_EXIST_CENTER;
        doThrow(new SignupException(error))
                .when(teacherService)
                .signup(request);
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                ));
    }

    @Test
    public void 교사회원가입_성공() throws Exception {
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
                .centerId(1L)
                .build();
        String url = "/signup/teacher";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isOk());
    }

    @Test
    public void 교사프로필정보조회_성공() throws Exception {
        // given
        String url = "/teacher/detail";
        TeacherDetailResponse response = new TeacherDetailResponse(Teacher.builder().build());
        doReturn(response)
                .when(teacherService)
                .findDetail(teacher.getId());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(teacher))
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(response)
                ));
    }

    @Test
    public void 교사프로필수정_실패_불완전한요청() throws Exception {
        // given
        String url = "/teacher/detail";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(url)
                        .file(multipartFile)
                        .header("Authorization", createJwtToken(teacher))
                        .param("changePhoneNum", "true")
                        .param("phoneNum", "newPhoneNum")
                        .param("emailAddress", "emailAddress")
                        .param("address", "address")
                        .param("detailAddress", "detailAddress")
        );
        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 교사프로필수정_실패_닉네임중복() throws Exception {
        // given
        String url = "/teacher/detail";
        UserErrorResult error = UserErrorResult.DUPLICATED_NICKNAME;
        doThrow(new UserException(error))
                .when(teacherService)
                .updateDetail(any(), any(UpdateTeacherDetailRequest.class));
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(url)
                        .file(multipartFile)
                        .header("Authorization", createJwtToken(teacher))
                        .param("name", "name")
                        .param("changePhoneNum", "true")
                        .param("phoneNum", "newPhoneNum")
                        .param("emailAddress", "emailAddress")
                        .param("address", "address")
                        .param("detailAddress", "detailAddress")
                        .param("nickname", "nickname")
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                ));
    }

    @Test
    public void 교사프로필수정_성공() throws Exception {
        // given
        String url = "/teacher/detail";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(url)
                        .file(multipartFile)
                        .header("Authorization", createJwtToken(teacher))
                        .param("name", "name")
                        .param("changePhoneNum", "true")
                        .param("phoneNum", "newPhoneNum")
                        .param("emailAddress", "emailAddress")
                        .param("address", "address")
                        .param("detailAddress", "detailAddress")
                        .param("nickname", "nickname")
        );
        // then
        result.andExpect(status().isOk());
    }

}
