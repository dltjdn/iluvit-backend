package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ParentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
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
                .password("password")
                .passwordCheck("password")
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
                .password("password")
                .passwordCheck("password")
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
                .password("password")
                .passwordCheck("password")
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
                .password("password")
                .passwordCheck("password")
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
                .password("password")
                .passwordCheck("password")
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


}
