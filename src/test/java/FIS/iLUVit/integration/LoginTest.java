package FIS.iLUVit.integration;

import FIS.iLUVit.dto.user.TokenRefreshRequest;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.TokenPair;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.TokenPairRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Nested
    @DisplayName("로그인")
    class login {

        // TODO 로그인아이디 틀림

        // TODO 비밀번호 틀림

        // TODO 최초 로그인 성공(튜토리얼 진행)

        // TODO 로그인 성공(튜토리얼 진행 X)
    }

    @Nested
    @DisplayName("RefreshToken를 이용한 토큰 재발급")
    class refresh {

        // TODO RefreshToken 유효성검사 실패

        // TODO DB와 불일치

        // TODO AccessToken 유효기간 남음

        // TODO 토큰 갱신 성공
    }

}
