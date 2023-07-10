package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReviewRepositoryTest {

    // TODO 학부모_아이디로_리뷰_조회

    // TODO 센터_아이디로_리뷰_조회

    // TODO 센터와_학부모로_리뷰_조회
}