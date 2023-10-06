package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ChildRepositoryTest {
    @Nested
    @DisplayName("findByIdAndParentWithCenter")
    class findByWithParentAndCenter {

        // TODO 정상 요청

        // TODO 잘못된 요청
    }

    // TODO find By User With Center

    @Nested
    @DisplayName("findByIdAndParent")
    class findByAndParent {

        // TODO 아이가 시설에 속한 경우

        // TODO 아이가 시설에 속하지 않은 경우
    }
}