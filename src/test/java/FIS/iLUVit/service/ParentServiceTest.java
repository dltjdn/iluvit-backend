package FIS.iLUVit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class ParentServiceTest {

    // TODO 학부모회원가입_성공

    // TODO 부모프로필정보조회_성공

    // TODO 부모프로필정보조회_성공

    @Nested
    @DisplayName("부모 프로필 수정")
    class updateDetail{

        // TODO 닉네임 중복

        // TODO 부모프로필정보수정_성공_번호변경X

        // TODO 부모프로필정보수정_성공_번호변경O
    }

    @Nested
    @DisplayName("시설 찜하기")
    class savePrefer{

        // TODO 이미 찜한 시설

        // TODO 잘못된 시설을 찜함

        // TODO 시설 찜하기 성공
    }

    @Nested
    @DisplayName("시설 찜 해제하기")
    class deletePrefer{

        // TODO 찜하지 않은 시설

        // TODO 찜 해제하기 성공
    }
}
