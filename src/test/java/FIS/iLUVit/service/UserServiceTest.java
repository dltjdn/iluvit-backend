package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService target;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthNumberRepository authNumberRepository;
    @Mock
    private AlarmRepository alarmRepository;
    @Spy
    private BCryptPasswordEncoder encoder;

    @Test
    public void 사용자기본정보_성공() {
        // given
        Parent parent = Creator.createParent("phoneNum");
        doReturn(Optional.of(parent));
        // when
        LoginResponse result = target.findUserInfo(parent.getId());
        // then
        assertThat(result.getId()).isEqualTo(parent.getId());
    }
}
