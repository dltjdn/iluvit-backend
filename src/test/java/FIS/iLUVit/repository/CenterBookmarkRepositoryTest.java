package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class CenterBookmarkRepositoryTest {

    // TODO find By User Id And Center Id
}
