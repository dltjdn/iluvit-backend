package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class CommentHeartRepositoryTest {

    // TODO 유저와_댓글로_좋아요_찾기
}