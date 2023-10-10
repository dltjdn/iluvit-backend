package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ScrapPostRepositoryTest {
    @Nested
    @DisplayName("findByScrapAndPost")
    class findByScrapAndPost{

        // TODO 요청이 정상적인 경우

        // TODO scrapPostId 오류
    }

    // TODO find By Scrap With Post
}
