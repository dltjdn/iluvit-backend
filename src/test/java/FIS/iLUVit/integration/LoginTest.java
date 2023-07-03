package FIS.iLUVit.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
