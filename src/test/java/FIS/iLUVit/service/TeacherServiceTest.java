package FIS.iLUVit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    // TODO 교사회원가입_실패_없는시설로등록

    // TODO 교사회원가입_성공_시설선택O

    // TODO 교사회원가입_성공_시설선택X

    // TODO 교사프로필조회_성공

    // TODO 교사프로필수정_실패_닉네임중복

    // TODO 교사프로필수정_실패_핸드폰변경시미인증

    // TODO 교사프로필수정_성공_핸드폰포함

    // TODO 교사프로필수정_성공_핸드폰미포함

    @Nested
    @DisplayName("시설에 등록신청")
    class AssignCenter{

        // TODO 이미 등록된 시설이 있음

        // TODO 시설 등록 신청 성공
    }

    @Nested
    @DisplayName("시설 스스로 탈주하기")
    class escapeCenter{

        // TODO 해당 시설에 속해 있지 않음 (사용자가 해당시설에 속해있지않음)

        // TODO 마지막 원장 탈주 실패 (일반 교사가 있는 시설에 마지막 원장의 탈주)

        // TODO 시설 탈주 성공
    }

    @Nested
    @DisplayName("교사관리 페이지에 필요한 교사들 정보 조회")
    class findTeacherApprovalList{

        // TODO 사용자가 원장이 아닌 경우

        // TODO 정상적인 요청
    }


    @Nested
    @DisplayName("교사승인")
    class acceptTeacher{

        // TODO 원장이 아닌 사용자의 요청

        // TODO 요청하지 않은 교사의 승인

        // TODO 정상적인 요청
    }

    @Nested
    @DisplayName("교사 승인신청 삭제/거절")
    class fireTeacher{

        // TODO 원장이 아닌 사용자의 요청

        // TODO 올바르지 않은 교사 삭제

        // TODO 존재하지 않는 교사 아이디

        // TODO 교사 삭제 성공
    }

    @Nested
    @DisplayName("원장권한부여")
    class mandateTeacher{

        // TODO 원장이 아닌 경우

        // TODO 시설에 속해있지 않은 교사

        // TODO 승인받지 않은 교사

        // TODO 권한 부여 성공
    }
    
    @Nested
    @DisplayName("원장권한 박탈")
    class demoteTeacher{

        // TODO 원장이 아닌 경우

        // TODO 해당 시설에 속해있지 않은 교사

        // TODO 원장 권한 박탈 성공
    }
}
