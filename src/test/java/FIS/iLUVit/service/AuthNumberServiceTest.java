package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.stub.MessageServiceStub;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthNumberServiceTest {

    @Mock
    private AuthNumberRepository authNumberRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BCryptPasswordEncoder encoder;
    @Spy
    private MessageServiceStub messageServiceStub;

    @InjectMocks
    private AuthNumberService target;

    private final String phoneNum = "phoneNumber";
    private final String authNum = "authNum";

    @Test
    public void 회원가입용인증번호받기_실패_이미가입된번호() {
        // given
        doReturn(Optional.of(Parent.builder().build())).when(userRepository).findByPhoneNumber(phoneNum);

        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForSignup(phoneNum, AuthKind.signup));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
    }

    @Test
    public void 회원가입용인증번호받기_실패_유효시간남음() {
        // given
        doReturn(Optional.empty()).when(userRepository).findByPhoneNumber(phoneNum);
        doReturn(Optional.of(createAuthNumber(AuthKind.signup)))
                .when(authNumberRepository)
                .findOverlap(phoneNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.sendAuthNumberForSignup(phoneNum, AuthKind.signup));

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
                .when(authNumberRepository)
                .findOverlap(phoneNum, AuthKind.signup);

        doReturn(createAuthNumber(AuthKind.signup))
                .when(authNumberRepository)
                .save(any(AuthNumber.class));

        // when
        AuthNumber result = target.sendAuthNumberForSignup(phoneNum, AuthKind.signup);

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

        doReturn(Optional.of(createAuthNumber(AuthKind.signup).setCreatedDateForTest(LocalDateTime.now().minusMinutes(6))))
                .when(authNumberRepository)
                .findOverlap(phoneNum, AuthKind.signup);

        doReturn(createAuthNumber(AuthKind.signup))
                .when(authNumberRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForSignup(phoneNum, AuthKind.signup);

        // then
        System.out.println("message = " + messageServiceStub.messageHistory);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.signup);

        // verify
        verify(authNumberRepository, times(1)).deleteExpiredNumber(phoneNum, AuthKind.signup);
    }

    @Test
    public void 회원가입용인증번호인증_실패_인증번호불일치() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        doReturn(Optional.empty())
                .when(authNumberRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.authenticateAuthNum(request));
        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.AUTHENTICATION_FAIL);
    }

    @Test
    public void 회원가입용인증번호인증_실패_인증번호만료() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        doReturn(Optional.of(createAuthNumber(AuthKind.signup).setCreatedDateForTest(LocalDateTime.now().minusMinutes(6))))
                .when(authNumberRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        // when
        AuthNumberException result = assertThrows(AuthNumberException.class,
                () -> target.authenticateAuthNum(request));

        // then
        assertThat(result.getErrorResult()).isEqualTo(AuthNumberErrorResult.EXPIRED);

    }

    @Test
    public void 회원가입용인증번호인증_성공() {
        // given
        AuthenticateAuthNumRequest request = new AuthenticateAuthNumRequest(phoneNum, authNum, AuthKind.signup);
        AuthNumber authNumber = createAuthNumber(AuthKind.signup);
        doReturn(Optional.of(authNumber))
                .when(authNumberRepository)
                .findByPhoneNumAndAuthNumAndAuthKind(phoneNum, authNum, AuthKind.signup);
        assertThat(authNumber.getAuthTime()).isNull();
        // when
        AuthNumber result = target.authenticateAuthNum(request);
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
                .when(authNumberRepository)
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
                .when(authNumberRepository)
                .findOverlap(phoneNum, AuthKind.findLoginId);

        doReturn(createAuthNumber(AuthKind.findLoginId))
                .when(authNumberRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForFindLoginId(phoneNum);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.findLoginId);

        // verify
        verify(authNumberRepository, times(0)).deleteExpiredNumber(phoneNum, AuthKind.findLoginId);
    }

    @Test
    public void 아이디를찾기위한인증번호받기_성공_제한시간만료() {
        // given
        doReturn(Optional.of(Parent.builder().build()))
                .when(userRepository)
                .findByPhoneNumber(phoneNum);

        doReturn(Optional.of(createAuthNumber(AuthKind.findLoginId).setCreatedDateForTest(LocalDateTime.now().minusMinutes(6))))
                .when(authNumberRepository)
                .findOverlap(phoneNum, AuthKind.findLoginId);

        doReturn(createAuthNumber(AuthKind.findLoginId))
                .when(authNumberRepository)
                .save(any(AuthNumber.class));
        // when
        AuthNumber result = target.sendAuthNumberForFindLoginId(phoneNum);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthKind()).isEqualTo(AuthKind.findLoginId);

        // verify
        verify(authNumberRepository, times(1)).deleteExpiredNumber(phoneNum, AuthKind.findLoginId);
    }


    private AuthNumber createAuthNumber(AuthKind authKind) {
        AuthNumber build = AuthNumber.builder()
                .id(-1L)
                .phoneNum(phoneNum)
                .authNum("1234")
                .authKind(authKind)
                .build();
        build.setCreatedDateForTest(LocalDateTime.now());
        return build;
    }


}
