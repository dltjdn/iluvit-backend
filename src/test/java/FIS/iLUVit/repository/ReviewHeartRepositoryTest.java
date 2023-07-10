package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReviewHeartRepositoryTest {

    // TODO 좋아요_저장

    // TODO 리뷰와_유저_아이디로_좋아요_찾기
}