package FIS.iLUVit.integration;

import FIS.iLUVit.controller.dto.TokenRefreshRequest;
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
import org.junit.jupiter.api.*;
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

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Value("${security.secretKey}")
    private String secretKey;
    @Autowired
    private UserRepository userRepository;
    @SpyBean
    private TokenPairRepository tokenPairRepository;

    @Autowired
    private UserService userService;
    @SpyBean
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private String loginId;
    private String password;
    private Parent parent;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();

        loginId = "loginId";
        password = "password";
        parent = Parent.builder()
                .loginId(loginId)
                .password(encoder.encode(password))
                .build();
    }

    @Nested
    @DisplayName("로그인")
    class login {
        @Test
        @DisplayName("[error] 로그인아이디 틀림")
        public void 로그인아이디틀림() throws Exception {
            // given
            userRepository.save(parent);
            String url = "/login";
            LoginRequest request = new LoginRequest("!@#!QAZ", password);
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("[error] 비밀번호 틀림")
        public void 비밀번호틀림() throws Exception {
            // given
            userRepository.save(parent);
            String url = "/login";
            LoginRequest request = new LoginRequest(loginId, "!@#QAZ");
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("[success] 로그인성공")
        public void 로그인성공() throws Exception {
            // given
            userRepository.save(parent);
            String url = "/login";
            LoginRequest request = new LoginRequest(loginId, password);
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            User user = userRepository.findByLoginId(loginId).get();
            TokenPair tokenPair = tokenPairRepository.findByUserId(user.getId()).get();
            LoginResponse response = user.getLoginInfo();
            response.setAccessToken("Bearer " + tokenPair.getAccessToken());
            response.setRefreshToken("Bearer " + tokenPair.getRefreshToken());
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(response)
                    ));
        }
    }

    @Nested
    @DisplayName("RefreshToken를 이용한 토큰 재발급")
    class refresh {
        @Test
        @DisplayName("[error] RefreshToken 유효성검사 실패")
        public void 유효성검사실패() {
            // given
            userRepository.save(parent);
            LoginResponse loginResponse = userService.login(new LoginRequest(loginId, password));
            TokenRefreshRequest request = new TokenRefreshRequest(loginResponse.getRefreshToken() + "trash");
            // when
            assertThrows(JWTVerificationException.class,
                    () -> userService.refresh(request));
            // then
        }

        @Test
        @DisplayName("[error] DB와 불일치")
        public void db와다름() {
            // given
            userRepository.save(parent);
            LoginResponse loginResponse = userService.login(new LoginRequest(loginId, password));
            doReturn(Optional.empty())
                    .when(tokenPairRepository)
                    .findByUserIdWithUser(any());
            TokenRefreshRequest request = new TokenRefreshRequest(loginResponse.getRefreshToken());
            // when
            assertThrows(JWTVerificationException.class,
                    () -> userService.refresh(request));
            // then
        }

        @Test
        @DisplayName("[error] AccessToken 유효기간 남음")
        public void 유효기간남음() {
            // given
            userRepository.save(parent);
            LoginResponse loginResponse = userService.login(new LoginRequest(loginId, password));
            TokenRefreshRequest request = new TokenRefreshRequest(loginResponse.getRefreshToken());
            // when
            LoginResponse result = userService.refresh(request);
            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("[success] 토큰 갱신 성공")
        public void 토큰갱신성공() {
            // given
            userRepository.save(parent);
            doReturn(createMockAccessToken(parent))
                    .when(jwtUtils)
                    .createAccessToken(any());
            LoginResponse loginResponse = userService.login(new LoginRequest(loginId, password));
            TokenRefreshRequest request = new TokenRefreshRequest(loginResponse.getRefreshToken());
            // when
            LoginResponse result = userService.refresh(request);
            // then
            TokenPair tokenPair = tokenPairRepository.findByUserId(parent.getId()).get();
            String dbAccessToken = tokenPair.getAccessToken();
            String dbRefreshToken = tokenPair.getRefreshToken();
            assertThat(result.getAccessToken()).isEqualTo(jwtUtils.addPrefix(dbAccessToken));
            assertThat(result.getRefreshToken()).isEqualTo(jwtUtils.addPrefix(dbRefreshToken));
        }
    }

    public String createMockAccessToken(User user) {
        String token = JWT.create()
                .withSubject("ILuvIt_AccessToken")
                .withExpiresAt(new Date(System.currentTimeMillis()))
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512(secretKey));
        return token;
    }


}
