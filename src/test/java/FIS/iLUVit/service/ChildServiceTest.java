package FIS.iLUVit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChildServiceTest {
    @Nested
    @DisplayName("부모 메인페이지 아이들정보")
    class 아이들정보{

        // TODO 아이 여러명

        // TODO 시설없는 아이 있음

        // TODO 아이 없음
    }

    // TODO 학부모관리페이지조회

    @Nested
    @DisplayName("아이/학부모 승인")
    class acceptChild {

        // TODO 승인 받지않은 교사의 요청

        // TODO 아이 아이디 잘못됨

        // TODO 부모의 아이 최초 승인

        // TODO 같은 시설 중복 승인
    }
    @Nested
    @DisplayName("아이/학부모 삭제/거절")
    class fireChild{

        // TODO 승인 받지않은 교사

        // TODO 아이 아이디 에러

        // TODO 마지막 아이 삭제 성공

        // TODO 아이 삭제 성공 아직 더 있음
    }


    @Nested
    @DisplayName("아이 추가")
    class saveChild{

        // TODO 시설 정보 잘못됨

        // TODO 아이추가 성공

        // TODO 아이추가 성공
    }
    @Nested
    @DisplayName("아이 프로필 조회")
    class findChildInfoDetail{

        // TODO 잘못된 아이 아이디

        // TODO 시설없는 경우

        // TODO 조회 성공
    }

    @Nested
    @DisplayName("아이 프로필 수정")
    class updateChild{

        // TODO 존재하지 않는 아이

        // TODO 아이 프로필 수정성공
    }

    @Nested
    @DisplayName("학부모/아이 시설 승인 요청")
    class 아이시설승인요청 {

        // TODO 잘못된 아이 아이디

        // TODO 시설에 속해있는 경우

        // TODO 잘못된 시설 정보

        // TODO 승인 요청 성공적
    }

    @Nested
    @DisplayName("아이 시설 탈퇴")
    class exitCenter{

        // TODO 아이 아이디가 잘못된 경우

        // TODO 속해있는 시설이 없는 경우

        // TODO 해당 시설에 사용자의 아이가 더 있는 경우

        // TODO 해당 시설에 사용자의 아이가 이제 없는 경우
    }

    @Nested
    @DisplayName("아이 삭제")
    class deleteChild{

        // TODO 잘못된 childId

        // TODO 해당시설의 마지막 아이

        // TODO 시설이 없는 아이 삭제
    }

    @Nested
    @DisplayName("시설에 다니는 아이없으면 북마크 삭제")
    class deleteBookmarkByCenter{

        // TODO 아직 남은 경우

        // TODO 마지막 삭제 경우
    }
}
