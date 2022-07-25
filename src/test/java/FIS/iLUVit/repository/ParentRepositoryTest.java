package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParentRepositoryTest {

    @Nested
    @DisplayName("자신이 신청 - 취소 - 대기한 설명회 목록 가져오기 ")
    class 자신이신청취소대기한설명회목록{

    }
}