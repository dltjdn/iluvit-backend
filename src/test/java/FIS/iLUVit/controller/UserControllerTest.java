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

    // TODO 사용자기본정보_성공







    // TODO 비밀번호변경_실패_비밀번호틀림







    // TODO 비밀번호변경_실패_비밀번호확인틀림








    // TODO 비밀번호변경_성공



    @Nested
    @DisplayName("로그인 중복확인")
    class checkLoginId{

        // TODO 로그인 아이디 5자이상








        // TODO 로그인아이디 중복








        // TODO 로그인아이디 안중복


    }
    
    @Nested
    @DisplayName("닉네임 중복확인")
    class checkNickname{

        // TODO 닉네임 글자 수 2~10자







        // TODO 닉네임 중복








        // TODO 닉네임 안중복


    }
    
}
