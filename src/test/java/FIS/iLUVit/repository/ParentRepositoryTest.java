package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParentRepositoryTest {
    @Nested
    @DisplayName("findWithChildren")
    class findWithChildren{

        // TODO 아이가 없는 경우

        // TODO 아이가 있는 경우

        // TODO 시설없는 아이 한명
    }


    @Nested
    @DisplayName("자신이 신청 - 취소 - 대기한 설명회 목록 가져오기 ")
    class 자신이신청취소대기한설명회목록{

        // TODO 자신이 신청한 설명회 가져오기 (학부모가 신청한 설명회)

        // TODO 자신이 취소한 설명회 가져오기 (학부모가 취소한 설명회)

        // TODO 자신이 대기중인 설명회 가져오기 (대기 신청 설명회)
    }

    // TODO find By Id With Child

    // TODO find By Id With Prefer With Center
}