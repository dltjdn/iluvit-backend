package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.UpdatePasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
        doReturn(Optional.of(parent))
                .when(userRepository)
                .findById(parent.getId());
        // when
        LoginResponse result = target.findUserInfo(parent.getId());
        // then
        assertThat(result.getId()).isEqualTo(parent.getId());
    }
    
    @Test
    public void 회원가입공통유효성검사_실패_비밀번호확인틀림() {
        // given
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.signupValidation("pwd", "pwdCheck", "loginId", "phoneNum", "nickName"));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_MATCH_PWDCHECK);
    } 
    
    @Test
    public void 회원가입공통유효성검사_실패_로그인아이디닉네임중복() {
        // given
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByLoginIdOrNickName("loginId", "nickName");
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.signupValidation("pwd", "pwd", "loginId", "phoneNum", "nickName"));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.DUPLICATED_NICKNAME);
    } 
    
    @Test
    public void 회원가입공통유효성검사_실패_핸드폰미인증() {
        // given
        doReturn(Optional.empty())
                .when(userRepository)
                .findByLoginIdOrNickName("loginId", "nickName");
        doReturn(Optional.empty())
                .when(authNumberRepository)
                .findAuthComplete("phoneNum", AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.signupValidation("pwd", "pwd", "loginId", "phoneNum", "nickName"));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_AUTHENTICATION);
    }
    @Test
    public void 회원가입공통유효성검사_실패_인증유효시간만료() {
        // given
        AuthNumber authNumber = Creator.createAuthNumber("phoneNum", "authNum", AuthKind.signup, LocalDateTime.now().minusSeconds(60 * 60 + 1));
        doReturn(Optional.empty())
                .when(userRepository)
                .findByLoginIdOrNickName("loginId", "nickName");
        doReturn(Optional.of(authNumber))
                .when(authNumberRepository)
                .findAuthComplete("phoneNum", AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.signupValidation("pwd", "pwd", "loginId", "phoneNum", "nickName"));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.EXPIRED);
    }

    @Test
    public void 회원가입공통유효성검사_성공() {
        // given
        AuthNumber authNumber = Creator.createAuthNumber("phoneNum", "authNum", AuthKind.signup, LocalDateTime.now().minusSeconds(60 * 60 - 1));
        doReturn(Optional.empty())
                .when(userRepository)
                .findByLoginIdOrNickName("loginId", "nickName");
        doReturn(Optional.of(authNumber))
                .when(authNumberRepository)
                .findAuthComplete("phoneNum", AuthKind.signup);
        // when
        String result = target.signupValidation("pwd", "pwd", "loginId", "phoneNum", "nickName");
        // then
        assertThat(encoder.matches("pwd", result)).isEqualTo(true);
    }

    @Test
    public void 비밀번호변경_실패_비밀번호틀림() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("pwd", "newPwd", "newPwd");
        Parent parent = Parent.builder()
                .id(1L)
                .password(encoder.encode("password"))
                .build();
        doReturn(Optional.of(parent))
                .when(userRepository)
                .findById(any());
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.updatePassword(parent.getId(), request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_MATCH_PWD);
    }

    @Test
    public void 비밀번호변경_실패_비밀번호확인틀림() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("password", "newPwd", "new");
        Parent parent = Parent.builder()
                .id(1L)
                .password(encoder.encode("password"))
                .build();
        doReturn(Optional.of(parent))
                .when(userRepository)
                .findById(any());
        // when
        SignupException result = assertThrows(SignupException.class,
                () -> target.updatePassword(parent.getId(), request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(SignupErrorResult.NOT_MATCH_PWDCHECK);
    }
    
    @Test
    public void 비밀번호변경_성공() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("password", "newPwd", "newPwd");
        Parent parent = Parent.builder()
                .id(1L)
                .password(encoder.encode("password"))
                .build();
        doReturn(Optional.of(parent))
                .when(userRepository)
                .findById(any());
        // when
        User result = target.updatePassword(parent.getId(), request);
        // then
        assertThat(encoder.matches("newPwd", result.getPassword())).isTrue();
    } 
}
