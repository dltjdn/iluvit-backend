package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.FindPasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.repository.AuthRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.stub.MessageServiceStub;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static FIS.iLUVit.Creator.createAuthNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {


    @Mock
    private AuthRepository authRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BCryptPasswordEncoder encoder;
    @Spy
    private MessageServiceStub messageServiceStub;

    @InjectMocks
    private AuthService target;

    private final String phoneNum = "phoneNum";
    private final String authNum = "authNum";

    @Test
    public void 회원가입용인증번호받기_실패_이미가입된번호() {
        // given
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForSignup(phoneNum));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
    }

    @Test
    public void 회원가입용인증번호받기_실패_유효시간남음() {
        // given
        doReturn(Optional.empty()).when(userRepository).findByPhoneNumber(phoneNum);
        doReturn(Optional.of(createAuthNumber(AuthKind.signup)))
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForSignup(phoneNum));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.YET_AUTHNUMBER_VALID);

    }

    @Test
    public void 회원가입용인증번호받기_성공_최초요청() {
        // given
        doReturn(Optional.empty())
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.empty())
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.signup);

        doReturn(createAuthNumber(AuthKind.signup))
                .when(authRepository)
                .save(any(AuthNumber.class));

        // when
        AuthNumber result = target.sendAuthNumberForSignup(phoneNum);

        // then
        System.out.println("message = " + messageServiceStub.messageHistory);
        assertThat(result).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.signup);
    }

    @Test
    public void 회원가입용인증번호받기_성공_제한시간만료() {
        // given
        doReturn(Optional.empty())
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.of(createAuthNumber(AuthKind.signup).setCreatedDateForTest(LocalDateTime.now().minusSeconds(target.getAuthValidTime() + 1))))
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.signup);

        doReturn(createAuthNumber(AuthKind.signup))
                .when(authRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForSignup(phoneNum);

        // then
        System.out.println("message = " + messageServiceStub.messageHistory);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.signup);

        // verify
        verify(authRepository, times(1)).deleteExpiredNumber(phoneNum, AuthKind.signup);
    }

    @Test
    public void 인증번호인증_실패_인증번호불일치() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        doReturn(Optional.empty())
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.authenticateAuthNum(null, request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.AUTHENTICATION_FAIL);
    }

    @Test
    public void 인증번호인증_실패_인증번호만료() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        doReturn(Optional.of(createAuthNumber(AuthKind.signup).setCreatedDateForTest(LocalDateTime.now().minusSeconds(target.getAuthValidTime() + 1))))
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.authenticateAuthNum(null, request));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.EXPIRED);

    }

    @Test
    public void 인증번호인증_성공() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumber authNumber = createAuthNumber(AuthKind.signup);
        doReturn(Optional.of(authNumber))
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        assertThat(authNumber.getAuthTime()).isNull();
        // when
        AuthNumber result = target.authenticateAuthNum(null, request);
        // then
        assertThat(result.getId()).isEqualTo(authNumber.getId());
        assertThat(result.getAuthTime()).isNotNull();
    }

    @Test
    public void 아이디를찾기위한인증번호받기_실패_가입되지않은핸드폰() {
        // given
        doReturn(Optional.empty())
                .when(userRepository)
                .findByPhoneNumber(phoneNum);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForFindLoginId(phoneNum));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_SIGNUP_PHONE);
    }

    @Test
    public void 아이디를찾기위한인증번호받기_실패_유효시간남음() {
        // given
        doReturn(Optional.of(Teacher.builder().build())).when(userRepository).findByPhoneNumber(phoneNum);
        doReturn(Optional.of(createAuthNumber(AuthKind.findLoginId)))
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.findLoginId);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForFindLoginId(phoneNum));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.YET_AUTHNUMBER_VALID);
    }

    @Test
    public void 아이디를찾기위한인증번호받기_성공_최초요청() {
        // given
        doReturn(Optional.of(Teacher.builder().build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.empty())
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.findLoginId);

        doReturn(createAuthNumber(AuthKind.findLoginId))
                .when(authRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForFindLoginId(phoneNum);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.findLoginId);

        // verify
        verify(authRepository, times(0)).deleteExpiredNumber(phoneNum, AuthKind.findLoginId);
    }

    @Test
    public void 아이디를찾기위한인증번호받기_성공_제한시간만료() {
        // given
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.of(createAuthNumber(AuthKind.findLoginId).setCreatedDateForTest(LocalDateTime.now().minusSeconds(target.getAuthValidTime() + 1))))
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.findLoginId);

        doReturn(createAuthNumber(AuthKind.findLoginId))
                .when(authRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForFindLoginId(phoneNum);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.findLoginId);

        // verify
        verify(authRepository, times(1)).deleteExpiredNumber(phoneNum, AuthKind.findLoginId);
    }

    @Test
    public void 아이디찾기_성공() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.findLoginId);
        doReturn(Optional.of(createAuthNumber(AuthKind.findLoginId)))
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.findLoginId);
        doReturn(Optional.of(Parent.builder().loginId("asdfg").build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);
        // when
        String result = target.findLoginId(request);
        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("a***g");
    }

    @Test
    public void 비밀번호찾기를위한인증번호받기_실패_아이디와휴대폰불일치() {
        // given
        String loginId = "loginId";
        doReturn(Optional.empty())
                .when(userRepository)
                .findByLoginIdAndPhoneNumber(loginId, phoneNum);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForFindPassword(loginId, phoneNum));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_MATCH_INFO);
    }

    @Test
    public void 비밀번호찾기를위한인증번호받기_성공() {
        // given
        String loginId = "loginId";
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByLoginIdAndPhoneNumber(loginId, phoneNum);

        doReturn(Optional.empty())
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.findPwd);

        doReturn(createAuthNumber(AuthKind.findPwd))
                .when(authRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber authNumber = target.sendAuthNumberForFindPassword(loginId, phoneNum);
        // then
        assertThat(authNumber).isNotNull();
        assertThat(authNumber.getAuthKind()).isEqualTo(AuthKind.findPwd);
    }

    @Test
    public void 비밀번호찾기실행_실패_비밀번호틀림() {
        // given
        FindPasswordRequest request = new FindPasswordRequest("loginId", phoneNum, authNum, "newPwd", "nwePwdCheck");
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.changePassword(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_MATCH_CHECKPWD);
    }

    @Test
    public void 비밀번호찾기실행_실패_핸드폰미인증() {
        // given
        FindPasswordRequest request = new FindPasswordRequest("loginId", phoneNum, authNum, "newPwd", "newPwd");

        doReturn(Optional.empty())
                .when(authRepository)
                .findAuthComplete(phoneNum, AuthKind.findPwd);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.changePassword(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_AUTHENTICATION);
    }

    @Test
    public void 비밀번호찾기실행_실패_인증시간만료() {
        // given
        FindPasswordRequest request = new FindPasswordRequest("loginId", phoneNum, authNum, "newPwd", "newPwd");
        doReturn(Optional.of(createAuthNumber(phoneNum, authNum, AuthKind.findPwd, LocalDateTime.now().minusSeconds(target.getAuthNumberValidTime() + 1))))
                .when(authRepository)
                .findAuthComplete(phoneNum, AuthKind.findPwd);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.changePassword(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.EXPIRED);
    }

    @Test
    public void 비밀번호찾기실행_실패_로그인아이디틀림() {
        // given
        FindPasswordRequest request = new FindPasswordRequest("loginId", phoneNum, authNum, "newPwd", "newPwd");
        doReturn(Optional.of(createAuthNumber(phoneNum, authNum, AuthKind.findPwd, LocalDateTime.now().minusSeconds(target.getAuthNumberValidTime() - 1))))
                .when(authRepository)
                .findAuthComplete(phoneNum, AuthKind.findPwd);
        doReturn(Optional.empty())
                .when(userRepository)
                .findByLoginIdAndPhoneNumber("loginId", phoneNum);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.changePassword(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_MATCH_INFO);
    }

    @Test
    public void 비밀번호찾기실행_성공() {
        // given
        FindPasswordRequest request = new FindPasswordRequest("loginId", phoneNum, authNum, "newPwd", "newPwd");
        AuthNumber authNumber = createAuthNumber(phoneNum, authNum, AuthKind.findPwd, LocalDateTime.now().minusSeconds(target.getAuthNumberValidTime() - 1));
        doReturn(Optional.of(authNumber))
                .when(authRepository)
                .findAuthComplete(phoneNum, AuthKind.findPwd);
        Parent parent = Creator.createParent(phoneNum);
        doReturn(Optional.of(Creator.createParent(phoneNum)))
                .when(userRepository)
                .findByLoginIdAndPhoneNumber("loginId", phoneNum);
        // when
        User user = target.changePassword(request);
        // then
        assertThat(user.getId()).isEqualTo(parent.getId());
        assertThat(encoder.matches("newPwd", user.getPassword())).isEqualTo(true);
        verify(authRepository, times(1)).delete(authNumber);
    }

    @Test
    public void 핸드폰변경을위한인증번호받기_실패_이미등록된핸드폰() {
        // given
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForChangePhone(any(Long.class), phoneNum));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
    }

    @Test
    public void 핸드폰변경을위한인증번호받기_성공_최초요청() {
        // given
        Long id = 1L;
        AuthNumber authNumber = createAuthNumber(AuthKind.updatePhoneNum);
        doReturn(Optional.empty())
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.empty())
                .when(authRepository)
                .findOverlap(phoneNum, AuthKind.updatePhoneNum);

        doReturn(createAuthNumber(AuthKind.updatePhoneNum))
                .when(authRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForChangePhone(id, phoneNum);
        // then
        assertThat(result.getId()).isEqualTo(authNumber.getId());
        assertThat(result.getAuthKind()).isEqualTo(authNumber.getAuthKind());
    }

    @Test
    public void 핸드폰변경을위한인증번호인증_실패_정보불일치() {
        // given
        Long userId = 1L;
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.updatePhoneNum);
        doReturn(Optional.empty())
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKindAndUserId(phoneNum, authNum, AuthKind.updatePhoneNum, userId);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.authenticateAuthNum(userId, request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.AUTHENTICATION_FAIL);
    }

    @Test
    public void 핸드폰변경을위한인증번호인증_성공() {
        // given
        Long userId = 1L;
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.updatePhoneNum);
        AuthNumber authNumber = createAuthNumber(AuthKind.updatePhoneNum);
        doReturn(Optional.of(authNumber))
                .when(authRepository)
                .findByPhoneNumAndAuthNumAndAuthKindAndUserId(phoneNum, authNum, AuthKind.updatePhoneNum, userId);
        // when
        AuthNumber result = target.authenticateAuthNum(userId, request);
        // then
        assertThat(result.getId()).isEqualTo(authNumber.getId());
        assertThat(result.getAuthTime()).isNotNull();
    }

    @Test
    public void 인증여부확인_실패_미완료() {
        // given
        AuthKind authKind = AuthKind.updatePhoneNum;
        doReturn(Optional.empty())
                .when(authRepository)
                .findAuthComplete(phoneNum, authKind);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.validateAuthNumber(phoneNum, authKind));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.NOT_AUTHENTICATION);
    }

    @Test
    public void 인증여부확인_실패_인증시간초과() {
        // given
        AuthKind authKind = AuthKind.updatePhoneNum;
        AuthNumber authNumber = createAuthNumber(phoneNum, authNum, authKind, LocalDateTime.now().minusSeconds(target.getAuthNumberValidTime() + 1));
        doReturn(Optional.of(authNumber))
                .when(authRepository)
                .findAuthComplete(phoneNum, authKind);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.validateAuthNumber(phoneNum, authKind));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.EXPIRED);
    }


}
