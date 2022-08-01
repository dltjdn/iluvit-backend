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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private TokenPairRepository tokenPairRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @SpyBean
    private JwtUtils jwtUtils;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

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
    class login{
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

//    @Nested
//    @DisplayName("refreshToken를 이용한 토큰 재발급")
//    class refresh{
//        @Test
//        @DisplayName("[error] refreshToken 유효성검사 실패")
//        public void 유효성검사실패() {
//            // given
//            userRepository.save(parent);
//            userService.login(new LoginRequest(loginId, password));
//            Mockito.doReturn()
//            User user = userRepository.findByLoginId(loginId).get();
//            String url = "/refresh";
//            TokenRefreshRequest request = new TokenRefreshRequest();
//            // when
//
//            // then
//
//        }
//    }


}
