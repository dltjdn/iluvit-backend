package FIS.iLUVit.integration;

import FIS.iLUVit.domain.TokenPair;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.TokenPairRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private TokenPairRepository tokenPairRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Spy
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Nested
    @DisplayName("로그인")
    class login{
        @Test
        @DisplayName("[error] 로그인아이디 틀림")
        public void 로그인아이디틀림() throws Exception {
            // given
            String url = "/login";
            LoginRequest request = new LoginRequest("!@#!QAZ", "asd");
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
            String url = "/login";
            LoginRequest request = new LoginRequest("asd", "!@#QAZ");
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
            String url = "/login";
            String loginId = "asd";
            String pwd = "asd";
            LoginRequest request = new LoginRequest(loginId, pwd);
            Mockito.doReturn()
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
}
