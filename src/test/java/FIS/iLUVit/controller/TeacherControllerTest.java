package FIS.iLUVit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    // TODO 회원가입 과정에서 center 정보 가져오기

    // TODO 교사회원가입_실패_닉네임길이

    // TODO 교사회원가입_실패_로그인아이디길이

    // TODO 교사회원가입_실패_없는시설로등록

    // TODO 교사회원가입_성공

    // TODO 교사프로필정보조회_성공

    // TODO 교사프로필수정_실패_불완전한요청

    // TODO 교사프로필수정_실패_닉네임중복

    // TODO 교사프로필수정_성공

    // TODO 시설에등록신청_실패_이미시설에등록됨

    // TODO 시설에틍록신청_성공

    @Nested
    @DisplayName("시설 스스로 탈주하기")
    class escapeCenter{

        // TODO 속해 있는 시설이 없는 경우

        // TODO 마지막 원장의 탈주 실패

        // TODO 시설 탈주 성공

    }

    @Nested
    @DisplayName("교사 관리페이지")
    class teacherApprovalList{

        // TODO 사용자가 원장이 아닌 경우

        // TODO 정상적인 요청

    }

    @Nested
    @DisplayName("교사 승인")
    class acceptTeacher{

        // TODO 원장이 아닌 사용자의 요청

        // TODO 올바르지 않은 교사 승인

        // TODO 교사 승인 성공

    }

    @Nested
    @DisplayName("교사 삭제/거절")
    class fireTeacher{

        // TODO 원장 아님

        // TODO 잘못된 교사 아이디

        // TODO 해당 시설에 속해 있지 않은 교사

        // TODO 교사 삭제/거절 성공

    }

    @Nested
    @DisplayName("원장권한 부여")
    class mandateTeacher{

        // TODO 원장 권한 없음

        // TODO 잘못된 teacherId

        // TODO 원장 권한 부여 성공

    }
    
    @Nested
    @DisplayName("원장권한 박탈")
    class demoteTeacher{

        // TODO 잘못된 교사 아이디

        // TODO 원장 권한 박탈 성공

    }
}
