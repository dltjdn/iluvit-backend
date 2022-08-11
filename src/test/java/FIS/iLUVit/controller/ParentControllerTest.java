package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ParentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ParentControllerTest {

    @InjectMocks
    private ParentController target;
    @Mock
    private ParentService parentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MockMultipartFile multipartFile;
    private Parent parent;
    private Center center;

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile("profileImg", name, "image", content);
        parent = Parent.builder()
                .id(1L)
                .nickName("nickname")
                .name("name")
                .build();
        center = Center.builder()
                .id(2L)
                .name("center")
                .build();
    }

    @Test
    public void 학부모회원가입_실패_비밀번호길이짧음() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("pwd")
                .passwordCheck("pwd")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
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
    public void 학부모회원가입_실패_일부필드null() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("pwd")
                .passwordCheck("pwd")
                .phoneNum("phoneNum")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";

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
    public void 학부모회원가입_실패_비밀번호확인틀림() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
        SignupErrorResult errorResult = SignupErrorResult.NOT_MATCH_PWDCHECK;
        doThrow(new SignupException(errorResult))
                .when(parentService)
                .signup(request);
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()))
                ));
    }

    @Test
    public void 학부모회원가입_실패_중복아이디닉네임() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
        SignupErrorResult errorResult = SignupErrorResult.DUPLICATED_NICKNAME;
        doThrow(new SignupException(errorResult))
                .when(parentService)
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
                        objectMapper.writeValueAsString(
                                new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()))
                ));
    }

    @Test
    public void 학부모회원가입_실패_핸드폰미인증() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
        AuthNumberErrorResult errorResult = AuthNumberErrorResult.NOT_AUTHENTICATION;
        doThrow(new AuthNumberException(errorResult))
                .when(parentService)
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
                        objectMapper.writeValueAsString(
                                new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()))
                ));
    }

    @Test
    public void 학부모회원가입_실패_인증번호만료() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
        AuthNumberErrorResult errorResult = AuthNumberErrorResult.EXPIRED;
        doThrow(new AuthNumberException(errorResult))
                .when(parentService)
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
                        objectMapper.writeValueAsString(
                                new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()))
                ));

    }

    @Test
    public void 학부모회원가입_성공() throws Exception {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .loginId("loginId")
                .password("asd123!@#")
                .passwordCheck("asd123!@#")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .name("name")
                .emailAddress("asd@asd")
                .address("address")
                .detailAddress("detailAddress")
                .interestAge(3)
                .build();
        String url = "/signup/parent";
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
    public void 학부모프로필조회_성공() throws Exception {
        // given
        String url = "/parent/detail";
        ParentDetailResponse response = new ParentDetailResponse();
        doReturn(response)
                .when(parentService)
                .findDetail(parent.getId());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(parent))
        );
        // then
        result.andExpect(content().json(
                objectMapper.writeValueAsString(new ParentDetailResponse())
        ));
    }

    @Test
    public void 학부모프로필수정_실패_불완전한요청() throws Exception {
        // given
        String url = "/parent/detail";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(url)
                        .file(multipartFile)
                        .header("Authorization", Creator.createJwtToken(parent))
                        .param("name", "name")
                        .param("changePhoneNum", "true")
                        .param("phoneNum", "newPhoneNum")
                        .param("address", "address")
                        .param("detailAddress", "detailAddress")
                        .param("emailAddress", "emailAddress")
                        .param("interestAge", "3")
                        .param("theme", objectMapper.writeValueAsString(Creator.createTheme()))
        );
        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 학부모프로필수정_성공() throws Exception {
        // given
        String url = "/parent/detail";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(url)
                        .file(multipartFile)
                        .header("Authorization", Creator.createJwtToken(parent))
                        .param("name", "name")
                        .param("nickname", "nickname")
                        .param("changePhoneNum", "true")
                        .param("phoneNum", "newPhoneNum")
                        .param("address", "address")
                        .param("detailAddress", "detailAddress")
                        .param("emailAddress", "emailAddress")
                        .param("interestAge", "3")
                        .param("theme", objectMapper.writeValueAsString(Creator.createTheme())));
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("시설찜하기")
    class savePrefer{
        @Test
        @DisplayName("[error] 이미 찜한 시설")
        public void 이미찜한시설() throws Exception {
            // given
            String url = "/parent/prefer/{centerId}";
            PreferErrorResult error = PreferErrorResult.ALREADY_PREFER;
            doThrow(new PreferException(error))
                    .when(parentService)
                    .savePrefer(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 잘못된 시설")
        public void centerIdError() throws Exception {
            // given
            String url = "/parent/prefer/{centerId}";
            PreferErrorResult error = PreferErrorResult.NOT_VALID_CENTER;
            doThrow(new PreferException(error))
                    .when(parentService)
                    .savePrefer(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", parent.getId())
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 찜하기 성공")
        public void 찜성공() throws Exception {
            // given
            String url = "/parent/prefer/{centerId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", parent.getId()));
            // then
            result.andExpect(status().isOk());
        }
    }
}
