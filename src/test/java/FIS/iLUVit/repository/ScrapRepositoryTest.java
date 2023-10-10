package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ScrapRepositoryTest {
    @Nested
    @DisplayName("findScrapsByUserWithScrapPosts")
    class findScrapsByUserWithScrapPosts {

        // TODO scrapPost가 있는 경우

        // TODO scrapPost가 없는 경우
    }

    @Nested
    @DisplayName("findScrapByIdAndUserId")
    class findScrapByIdAndUserId{

        // TODO 결과가 있을 경우

        // TODO 결과가 없는 경우
    }
}
