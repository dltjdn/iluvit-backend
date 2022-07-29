package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.UpdatePasswordRequest;
import FIS.iLUVit.controller.dto.UserInfoResponse;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;

import static org.mockito.Mockito.*;
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
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        parent = Creator.createParent(1L);
    }

    @Test
    public void 사용자기본정보_성공() throws Exception {
        // given
        String url = "/user/info";
        doReturn(new UserInfoResponse())
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
                        objectMapper.writeValueAsString(new UserInfoResponse())
                ));
    }

    @Test
    public void 비밀번호변경_실패_비밀번호틀림() throws Exception {
        // given
        String url = "/user/password";
        UpdatePasswordRequest request = UpdatePasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("newPwd")
                .newPwdCheck("newPwdCheck")
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
        String url = "/user/password";
        UpdatePasswordRequest request = UpdatePasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("newPwd")
                .newPwdCheck("newPwdCheck")
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
        String url = "/user/password";
        UpdatePasswordRequest request = UpdatePasswordRequest
                .builder()
                .originPwd("originPwd")
                .newPwd("newPwd")
                .newPwdCheck("newPwdCheck")
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
}
