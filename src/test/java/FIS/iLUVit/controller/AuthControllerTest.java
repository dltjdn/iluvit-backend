package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
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

    // TODO 회원가입인증번호받기_실패_이미가입된번호












    // TODO 인증번호받기_실패_유효시간남음













    // TODO 인증번호받기_성공













    // TODO 인증번호인증_실패_인증정보불일치















    // TODO 인증번호인증_실패_인증번호만료















    // TODO 인증번호인증_성공













    // TODO 인증번호인증_성공_핸드폰변경














    // TODO 아이디찾기인증번호받기_실패_가입하지않은핸드폰
















    // TODO 아이디찾기인증번호받기_성공
















    // TODO 아이디찾기_성공

















    // TODO 비밀번호찾기인증번호받기_실패_아이디휴대폰불일치

















    // TODO 비밀번호찾기인증번호받기_성공
















    // TODO 핸드폰변경을위한인증번호받기_실패_토큰없음

















    // TODO 핸드폰변경을위한인증번호받기_성공














    // TODO 핸드폰변경을위한인증번호받기_성공

    @Nested
    @DisplayName("비밀번호 찾기")
    class findPassword{

        // TODO 비밀번호 조건 불만족












        // TODO 비밀번호확인 불일치













        // TODO 사용자 정보 불일치
















        // TODO 비밀번호찾기 완료

    }

}
