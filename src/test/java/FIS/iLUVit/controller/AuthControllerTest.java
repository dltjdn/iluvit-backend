package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.domain.iluvit.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.AuthService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class AuthControllerTest {

    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController target;

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
        final String url = "/auth/signup";
        AuthNumberErrorResult error = AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER;

        doThrow(new AuthNumberException(error))
                .when(authService)
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
        final String url = "/auth/signup";
        AuthNumberErrorResult error = AuthNumberErrorResult.YET_AUTHNUMBER_VALID;

        doThrow(new AuthNumberException(error))
                .when(authService)
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
        final String url = "/auth/signup";

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
        final String url = "/auth";
        AuthNumRequest request = new AuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumberErrorResult error = AuthNumberErrorResult.AUTHENTICATION_FAIL;
        doThrow(new AuthNumberException(error))
                .when(authService)
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
        final String url = "/auth";
        AuthNumRequest request = new AuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumberErrorResult error = AuthNumberErrorResult.EXPIRED;
        doThrow(new AuthNumberException(error))
                .when(authService)
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
        final String url = "/auth";
        AuthNumRequest request = new AuthNumRequest(phoneNum, authNum, AuthKind.signup);

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
        final String url = "/auth";
        AuthNumRequest request = new AuthNumRequest(phoneNum, authNum, AuthKind.updatePhoneNum);

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
        final String url = "/auth/loginid";
        AuthNumberErrorResult error = AuthNumberErrorResult.NOT_SIGNUP_PHONE;
        doThrow(new AuthNumberException(error))
                .when(authService)
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
        final String url = "/auth/loginid";
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
        String url = "/auth/loginid";
        String loginId = "lo***Id";
        AuthNumRequest request = new AuthNumRequest(phoneNum, authNum, AuthKind.findLoginId);
        doReturn(loginId)
                .when(authService)
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
        String url = "/auth/password";
        AuthNumberErrorResult error = AuthNumberErrorResult.NOT_MATCH_INFO;
        doThrow(new AuthNumberException(error))
                .when(authService)
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
        String url = "/auth/password";
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
        String url = "/auth/phonenumber";
        AuthNumberErrorResult error = AuthNumberErrorResult.AUTHENTICATION_FAIL;
        doThrow(new AuthNumberException(error))
                .when(authService)
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
        String url = "/auth/phonenumber";
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

    @Nested
    @DisplayName("비밀번호 찾기")
    class findPassword{
        @Test
        @DisplayName("[error] 비밀번호 조건 불만족")
        public void 비번확인틀림() throws Exception {
            // given
            String url = "/auth/password";
            FindPasswordRequest request = new FindPasswordRequest(user.getLoginId(), user.getPhoneNumber(), "1234", "asd", "asd");
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[error] 비밀번호확인 불일치")
        public void 비번확인불일치() throws Exception {
            // given
            String url = "/auth/password";
            AuthNumberErrorResult error = AuthNumberErrorResult.NOT_MATCH_CHECKPWD;
            FindPasswordRequest request = new FindPasswordRequest(user.getLoginId(), user.getPhoneNumber(), "1234", "asdf1234!", "asdf12345!");
            doThrow(new AuthNumberException(error))
                    .when(authService)
                    .changePassword(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)));
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 사용자 정보 불일치")
        public void 사용자정보불일치() throws Exception {
            // given
            String url = "/auth/password";
            AuthNumberErrorResult error = AuthNumberErrorResult.NOT_MATCH_INFO;
            FindPasswordRequest request = new FindPasswordRequest(user.getLoginId(), user.getPhoneNumber(), "1234", "asdf1234!", "asdf1234!");
            doThrow(new AuthNumberException(error))
                    .when(authService)
                    .changePassword(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 비밀번호찾기 완료")
        public void 비번찾기완료() throws Exception {
            // given
            String url = "/auth/password";
            FindPasswordRequest request = new FindPasswordRequest(user.getLoginId(), user.getPhoneNumber(), "1234", "asdf1234!", "asdf1234!");
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

}
