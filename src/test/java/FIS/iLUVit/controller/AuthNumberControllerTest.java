package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.AuthNumberService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthNumberControllerTest {

    @Mock
    private AuthNumberService authNumberService;
    @InjectMocks
    private AuthNumberController target;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User user;
    private final String phoneNum = "phoneNumber";
    private final String authNum = "authNumber";

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        user = Parent.builder()
                .id(1L)
                .phoneNumber(phoneNum)
                .name("parent")
                .build();
    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 회원가입인증번호받기_실패_이미가입된번호() throws Exception {
        // given
        final String url = "/authNumber/signup";
        AuthNumberErrorResult error = AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER;

        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .sendAuthNumberForSignup(phoneNum);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 인증번호받기_실패_유효시간남음() throws Exception {
        // given
        final String url = "/authNumber/signup";
        AuthNumberErrorResult error = AuthNumberErrorResult.YET_AUTHNUMBER_VALID;

        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .sendAuthNumberForSignup(phoneNum);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 인증번호받기_성공() throws Exception {
        // given
        final String url = "/authNumber/signup";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 인증번호인증_실패_인증정보불일치() throws Exception {
        // given
        final String url = "/authNumber";
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumberErrorResult error = AuthNumberErrorResult.AUTHENTICATION_FAIL;
        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .authenticateAuthNum(null, request);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 인증번호인증_실패_인증번호만료() throws Exception {
        // given
        final String url = "/authNumber";
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumberErrorResult error = AuthNumberErrorResult.EXPIRED;
        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .authenticateAuthNum(null, request);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );

    }

    @Test
    public void 인증번호인증_성공() throws Exception {
        // given
        final String url = "/authNumber";
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 인증번호인증_성공_핸드폰변경() throws Exception {
        // given
        final String url = "/authNumber";
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.updatePhoneNum);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken(user))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 아이디찾기인증번호받기_실패_가입하지않은핸드폰() throws Exception {
        // given
        final String url = "/authNumber/loginId";
        AuthNumberErrorResult error = AuthNumberErrorResult.NOT_SIGNUP_PHONE;
        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .sendAuthNumberForFindLoginId(phoneNum);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 아이디찾기인증번호받기_성공() throws Exception {
        // given
        final String url = "/authNumber/loginId";
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 아이디찾기_성공() throws Exception {
        // given
        Gson gson = new Gson();
        String url = "/findLoginId";
        String loginId = "lo***Id";
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.findLoginId);
        doReturn(loginId)
                .when(authNumberService)
                .findLoginId(request);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(loginId));
    }

    @Test
    public void 비밀번호찾기인증번호받기_실패_아이디휴대폰불일치() throws Exception {
        // given
        String url = "/authNumber/password";
        AuthNumberErrorResult error = AuthNumberErrorResult.NOT_MATCH_INFO;
        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .sendAuthNumberForFindPassword("loginId", "phoneNumber");
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("loginId", "loginId")
                        .param("phoneNumber", "phoneNumber")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 비밀번호찾기인증번호받기_성공() throws Exception {
        // given
        String url = "/authNumber/password";
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("loginId", "loginId")
                        .param("phoneNumber", "phoneNumber")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 핸드폰변경을위한인증번호받기_실패_토큰없음() throws Exception {
        // given
        String url = "/user/authNumber/phoneNumber";
        AuthNumberErrorResult error = AuthNumberErrorResult.AUTHENTICATION_FAIL;
        doThrow(new AuthNumberException(error))
                .when(authNumberService)
                .sendAuthNumberForChangePhone(null, phoneNum);
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                ));
    }

    @Test
    public void 핸드폰변경을위한인증번호받기_성공() throws Exception {
        // given
        String url = "/user/authNumber/phoneNumber";
        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(user))
                        .param("phoneNumber", phoneNum)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        resultActions.andExpect(status().isOk());
    }

}
