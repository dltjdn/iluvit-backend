package FIS.iLUVit.integration;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.repository.TokenPairRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @SpyBean
    private JwtUtils jwtUtils;
    @SpyBean
    private BCryptPasswordEncoder encoder;
    @SpyBean
    private UserRepository userRepository;

    private String loginId = "loginId";
    private String password = "password";
    @Nested
    @DisplayName("로그인")
    class login{

//        @Test
//        @Transactional
//        @DisplayName("[error] 아이디틀림")
//        public void 아이디틀림() {
//            // given
//            Parent parent = Parent.builder()
//                    .loginId(loginId)
//                    .password(encoder.encode(password))
//                    .build();
//            userRepository.save(parent);
//            LoginRequest request = new LoginRequest("@@@", password);
//            // when
//            LoginResponse login = userService.login(request);
//            // then
//
//        }
    }
}
