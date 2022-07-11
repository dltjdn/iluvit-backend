package FIS.iLUVit.service;



import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthNumberServiceTest {

    @Mock
    private AuthNumberRepository authNumberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Spy
    private DefaultMessageService messageService = NurigoApp.INSTANCE.initialize();

    @InjectMocks
    private AuthNumberService target;

    @Test
    public void 인증번호받기_실패_이미가입된번호() {
        // given
        doReturn(Parent.builder().build()).when(userRepository).findByPhoneNumber("01067150071");

        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForSignup("01067150071", AuthKind.signup));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
    }
}
