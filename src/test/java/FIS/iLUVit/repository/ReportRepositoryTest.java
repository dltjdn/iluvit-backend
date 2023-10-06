package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReportRepositoryTest {

    // TODO 타겟 아이디로 조회
}