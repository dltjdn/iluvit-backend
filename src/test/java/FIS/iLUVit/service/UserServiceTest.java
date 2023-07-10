package FIS.iLUVit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // TODO 사용자기본정보_성공

    // TODO 회원가입공통유효성검사_실패_비밀번호확인틀림

    // TODO 회원가입공통유효성검사_실패_로그인아이디닉네임중복

    // TODO 회원가입공통유효성검사_실패_핸드폰미인증

    // TODO 회원가입공통유효성검사_실패_인증유효시간만료

    // TODO 회원가입공통유효성검사_성공

    // TODO 비밀번호변경_실패_비밀번호틀림

    // TODO 비밀번호변경_실패_비밀번호확인틀림

    // TODO 비밀번호변경_성공

    @Nested
    @DisplayName("로그인아이디 중복 확인")
    class checkLoginId{

        // TODO 이미 존재하는 로그인 아이디

        // TODO 사용 가능한 로그인 아이디
    }

    @Nested
    @DisplayName("닉네임 중복 확인")
    class checkNickname{

        // TODO 이미 존재하는 닉네임

        // TODO 사용 가능한 닉네임
    }
}
