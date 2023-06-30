package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
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

    // TODO 회원가입용인증번호받기_실패_이미가입된번호

    // TODO 회원가입용인증번호받기_실패_유효시간남음

    // TODO 회원가입용인증번호받기_성공_최초요청

    // TODO 회원가입용인증번호받기_성공_제한시간만료

    // TODO 인증번호인증_실패_인증번호불일치

    // TODO 인증번호인증_실패_인증번호만료

    // TODO 인증번호인증_성공

    // TODO 아이디를찾기위한인증번호받기_실패_가입되지않은핸드폰

    // TODO 아이디를찾기위한인증번호받기_실패_유효시간남음

    // TODO 아이디를찾기위한인증번호받기_성공_최초요청

    // TODO 아이디를찾기위한인증번호받기_성공_제한시간만료

    // TODO 아이디찾기_성공

    // TODO 비밀번호찾기를위한인증번호받기_실패_아이디와휴대폰불일치

    // TODO 비밀번호찾기를위한인증번호받기_성공

    // TODO 비밀번호찾기실행_실패_비밀번호틀림

    // TODO 비밀번호찾기실행_실패_핸드폰미인증

    // TODO 비밀번호찾기실행_실패_인증시간만료

    // TODO 비밀번호찾기실행_실패_로그인아이디틀림

    // TODO 비밀번호찾기실행_성공

    // TODO 핸드폰변경을위한인증번호받기_실패_이미등록된핸드폰

    // TODO 핸드폰변경을위한인증번호받기_성공_최초요청

    // TODO 핸드폰변경을위한인증번호인증_실패_정보불일치

    // TODO 핸드폰변경을위한인증번호인증_성공

    // TODO 인증여부확인_실패_미완료

    // TODO 인증여부확인_실패_인증시간초과
}
