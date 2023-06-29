package FIS.iLUVit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChildControllerTest {

    // TODO 아이들정보반환

    // TODO 아이추가센터정보조회

    @Nested
    @DisplayName("아이승인")
    class acceptChild {

        // TODO 승인받지 않은 교사 요청

        // TODO 아이 아이디 에러

        // TODO 아이 승인 성공

    }

    @Nested
    @DisplayName("아이삭제")
    class fireChild {

        // TODO 승인받지 않은 교사

        // TODO 아이 아이디 에러

        // TODO 아이 삭제 성공

    }

    @Nested
    @DisplayName("아이 프로필 수정")
    class updateChild {

        // TODO 식별자값 에러

        // TODO 요청 validation error

        // TODO 수정 성공

    }

    @Nested
    @DisplayName("학부모/아이 시설 승인 요청")
    class mappingCenter {

        // TODO 잘못된 아이 정보

        // TODO 아이가 시설에 속해있는 경우

        // TODO 잘못된 시설 정보

        // TODO 승인 요청 성공

    }

    @Nested
    @DisplayName("아이의 시설 탈퇴")
    class exitCenter {

        // TODO 잘못된 요청

        // TODO 정상 요청

    }

    @Nested
    @DisplayName("아이삭제")
    class deleteChild{

        // TODO 잘못된 childId

        // TODO 아이 삭제 성공

    }

    @Nested
    @DisplayName("아이추가")
    class saveChild{

        // TODO 불완전한 요청

        // TODO 잘못된 센터로의 접근

        // TODO 아이추가 성공

    }

    @Nested
    @DisplayName("아이 프로필 조회")
    class findChildInfoDetail{

        // TODO 잘못된 아이 ID

        // TODO 아이 프로필 조회 성공

    }

    @Nested
    @DisplayName("시설에 등록된 아이들정보 조회")
    class approvalList{

        // TODO 승인되지 않은 교사의 요청

        // TODO 아이들 정보 조회 성공

    }
}