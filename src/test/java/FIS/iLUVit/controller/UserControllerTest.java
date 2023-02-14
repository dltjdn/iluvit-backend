package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.user.CheckNicknameRequest;
import FIS.iLUVit.dto.user.PasswordRequest;
import FIS.iLUVit.dto.user.UserResponse;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController target;
    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Parent parent;

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        parent = Creator.createParent(1L);
    }

    @Test
    public void 사용자기본정보_성공() throws Exception {
        // given
        String url = "/user";
        doReturn(new UserResponse())
                .when(userService)
                .findUserInfo(any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(parent))
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new UserResponse())
                ));
    }

    @Test
    public void 비밀번호변경_실패_비밀번호틀림() throws Exception {
        // given
        String url = "/password";
        PasswordRequest request = PasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("asd123!@#")
                .newPwdCheck("asd123!@#")
                .build();
        SignupErrorResult error = SignupErrorResult.NOT_MATCH_PWD;
        doThrow(new SignupException(error))
                .when(userService)
                .updatePassword(any(), any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
                        .header("Authorization", Creator.createJwtToken(parent))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 비밀번호변경_실패_비밀번호확인틀림() throws Exception {
        // given
        String url = "/password";
        PasswordRequest request = PasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("asd123!@#")
                .newPwdCheck("asd123!@")
                .build();
        SignupErrorResult error = SignupErrorResult.NOT_MATCH_PWDCHECK;
        doThrow(new SignupException(error))
                .when(userService)
                .updatePassword(any(), any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
                        .header("Authorization", Creator.createJwtToken(parent))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage())))
                );
    }

    @Test
    public void 비밀번호변경_성공() throws Exception {
        // given
        String url = "/password";
        PasswordRequest request = PasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("asd123!@#")
                .newPwdCheck("asd123!@#")
                .build();
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(url)
                        .header("Authorization", Creator.createJwtToken(parent))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("로그인 중복확인")
    class checkLoginId{

        @Test
        @DisplayName("[error] 로그인 아이디 5자이상")
        public void 다섯자이상() throws Exception {
            // given
            String url = "/check-loginid";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("loginId", "asd")
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[error] 로그인아이디 중복")
        public void 아이디중복() throws Exception {
            // given
            String url = "/check-loginid";
            UserErrorResult error = UserErrorResult.ALREADY_LOGINID_EXIST;
            doThrow(new UserException(error))
                    .when(userService)
                    .checkLoginId(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("loginId", "asdfg")
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 로그인아이디 안중복")
        public void 안중복() throws Exception {
            // given
            String url = "/check-loginid";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("loginId", "asdfg")
            );
            // then
            result.andExpect(status().isOk());
        }
    }
    
    @Nested
    @DisplayName("닉네임 중복확인")
    class checkNickname{
        @Test
        @DisplayName("[error] 닉네임 글자 수 2~10자")
        public void 닉네임글자수() throws Exception {
            // given
            String url = "/check-nickname";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("nickname", "10자이상의닉네임이지롱롱롱")
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[error] 닉네임 중복")
        public void 닉네임중복() throws Exception {
            // given
            String url = "/check-nickname";
            UserErrorResult error = UserErrorResult.ALREADY_NICKNAME_EXIST;
            CheckNicknameRequest request = new CheckNicknameRequest("asd");
            doThrow(new UserException(error))
                    .when(userService)
                    .checkNickname(request);
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("nickname", request.getNickname())
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 닉네임 안중복")
        public void 닉네임안중복() throws Exception {
            // given
            String url = "/check-nickname";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .param("nickname", "asd")
            );
            // then
            result.andExpect(status().isOk());
        }
    }
    
}
