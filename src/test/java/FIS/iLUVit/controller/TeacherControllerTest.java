package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    private Teacher director;
    private MockMultipartFile multipartFile;

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        teacher = Creator.createTeacher(1L, "teacher", null, Auth.TEACHER, null);
        director = createTeacher(2L, "director", Center.builder().build(), Auth.DIRECTOR, Approval.ACCEPT);
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
    }

    @Test
    public void 회원가입과정에서center정보가져오기() throws Exception {
        // given
        String url = "/teacher/search/center?page=0&size=5";
        CenterRequest request = CenterRequest.builder()
                .sido("서울시")
                .sigungu("금천구")
                .centerName("")
                .build();
        List<CenterDto> content = List.of(CenterDto.builder()
                .id(1L)
                .name("name")
                .address("address")
                .build());
        PageRequest pageable = PageRequest.of(0, 5);
        SliceImpl<CenterDto> response = new SliceImpl<>(content, pageable, false);
        doReturn(response)
                .when(teacherService)
                .findCenterForTeacherSignup(request, pageable);
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("sido", request.getSido())
                        .param("sigungu", request.getSigungu())
                        .param("centerName", request.getCenterName())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void 교사회원가입_실패_닉네임길이() throws Exception {
        // given
        SignupTeacherRequest request = SignupTeacherRequest.builder()
                .loginId("log")
                .password("password")
                .passwordCheck("password")
                .phoneNum("phoneNum")
                .nickname("nickName123123")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .centerId(1L)
                .build();
        String url = "/teacher/signup";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
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
        String url = "/teacher/signup";
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
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .centerId(1L)
                .build();
        String url = "/teacher/signup";
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
        result.andExpect(status().isIAmATeapot())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                ));
    }

    @Test
    public void 교사회원가입_성공() throws Exception {
        // given
        SignupTeacherRequest request = SignupTeacherRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .centerId(1L)
                .build();
        String url = "/teacher/signup";
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
        String url = "/teacher";
        TeacherDetailResponse response = new TeacherDetailResponse(Teacher.builder().build(),Teacher.builder().build().getProfileImagePath());
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
        String url = "/teacher";
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
        String url = "/teacher";
        SignupErrorResult error = SignupErrorResult.DUPLICATED_NICKNAME;
        doThrow(new SignupException(error))
                .when(teacherService)
                .updateDetail(any(), any(TeacherDetailRequest.class));
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
        String url = "/teacher";
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

    @Test
    public void 시설에등록신청_실패_이미시설에등록됨() throws Exception {
        // given
        String url = "/teacher/center/{centerId}";
        SignupErrorResult error = SignupErrorResult.ALREADY_BELONG_CENTER;
        doThrow(new SignupException(error))
                .when(teacherService)
                .assignCenter(any(), any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, "123")
                        .header("Authorization", createJwtToken(teacher))
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                ));
    }

    @Test
    public void 시설에틍록신청_성공() throws Exception {
        // given
        String url = "/teacher/center/{centerId}";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, "123")
                        .header("Authorization", createJwtToken(teacher))
        );
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("시설 스스로 탈주하기")
    class escapeCenter{

        @Test
        @DisplayName("[error] 속해있는시설이없는경우")
        public void 속해있는시설이없는경우() throws Exception {
            // given
            String url = "/teacher/center";
            SignupErrorResult error = SignupErrorResult.NOT_BELONG_CENTER;
            doThrow(new SignupException(error))
                    .when(teacherService)
                    .escapeCenter(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url)
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 마지막원장의탈주실패")
        public void 마지막원장의탈주실패() throws Exception {
            // given
            String url = "/teacher/center";
            SignupErrorResult error = SignupErrorResult.HAVE_TO_MANDATE;
            doThrow(new SignupException(error))
                    .when(teacherService)
                    .escapeCenter(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url)
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 시설탤주성공")
        public void 시설탈주성공() throws Exception {
            // given
            String url = "/teacher/center";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url)
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("교사 관리페이지")
    class teacherApprovalList{

        @Test
        @DisplayName("[error] 사용자가 원장이 아닌 경우")
        public void 사용자가원장이아닌경우() throws Exception {
            // given
            String url = "/teacher/approval";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .findTeacherApprovalList(teacher.getId());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 정상적인 요청")
        public void 정상적인요청() throws Exception {
            // given
            List<TeacherInfoForAdminDto> teacherInfoForAdmin = new ArrayList<>();
            String url = "/teacher/approval";
            doReturn(teacherInfoForAdmin)
                    .when(teacherService)
                    .findTeacherApprovalList(director.getId());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("교사 승인")
    class acceptTeacher{

        @Test
        @DisplayName("[error] 원장이 아닌 사용자의 요청")
        public void 원장아닌요청() throws Exception {
            // given
            String url = "/teacher/{teacherId}/accept";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .acceptTeacherRegistration(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 올바르지 않은 교사 승인")
        public void 승인교사에러() throws Exception {
            // given
            String url = "/teacher/{teacherId}/accept";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .acceptTeacherRegistration(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, director.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 교사승인 성공")
        public void 교사승인성공() throws Exception {
            // given
            String url = "/teacher/{teacherId}/accept";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("교사 삭제/거절")
    class fireTeacher{
        @Test
        @DisplayName("[error] 원장아님")
        public void 원장아님() throws Exception {
            // given
            String url = "/teacher/{teacherId}/reject";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .fireTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 잘못된교사아이디")
        public void 잘못된교사아이디() throws Exception {
            // given
            String url = "/teacher/{teacherId}/reject";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .fireTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, director.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 해당시설에속해있지않은교사")
        public void 속해있지않은교사() throws Exception {
            // given
            String url = "/teacher/{teacherId}/reject";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .fireTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, director.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 교사 삭제/거절 성공")
        public void 교사삭제성공() throws Exception {
            // given
            String url = "/teacher/{teacherId}/reject";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, director.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("원장권한 부여")
    class mandateTeacher{
        @Test
        @DisplayName("[error] 원장권한 없음")
        public void 원장권한없음() throws Exception {
            // given
            String url = "/teacher/{teacherId}/mandate";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .mandateTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 잘못된 teacherId")
        public void teacherIdError() throws Exception {
            // given
            String url = "/teacher/{teacherId}/mandate";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .mandateTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }
        @Test
        @DisplayName("[success] 원장권한부여 성공")
        public void 원장권한부여성공() throws Exception {
            // given
            String url = "/teacher/{teacherId}/mandate";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isOk());
        }
    }
    
    @Nested
    @DisplayName("원장권한 박탈")
    class demoteTeacher{
        @Test
        @DisplayName("[error] 잘못된 교사 아이디")
        public void 잘못된교사아이디() throws Exception {
            // given
            String url = "/teacher/{teacherId}/demote";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(teacherService)
                    .demoteTeacher(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 원장권한 박탈 성공")
        public void 박탈성공() throws Exception {
            // given
            String url = "/teacher/{teacherId}/demote";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, teacher.getId())
                            .header("Authorization", createJwtToken(director))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

}
