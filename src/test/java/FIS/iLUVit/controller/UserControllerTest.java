package FIS.iLUVit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    // TODO 사용자기본정보_성공

    // TODO 비밀번호변경_실패_비밀번호틀림

    // TODO 비밀번호변경_실패_비밀번호확인틀림

    // TODO 비밀번호변경_성공

    @Nested
    @DisplayName("로그인 중복확인")
    class checkLoginId{

        // TODO 로그인 아이디 5자이상

        // TODO 로그인아이디 중복

        // TODO 로그인아이디 안중복

    }
    @Nested
    @DisplayName("닉네임 중복확인")
    class checkNickname{

        // TODO 닉네임 글자 수 2~10자

        // TODO 닉네임 중복

        // TODO 닉네임 안중복

    }
}
