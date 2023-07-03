package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class CommentRepositoryTest {

    // TODO 유저로_댓글_찾기

    // TODO 게시글_유저_익명정보로_댓글1개_찾기
}