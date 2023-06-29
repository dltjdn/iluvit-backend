package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class BoardRepositoryTest {

    // TODO 게시판 저장

    // TODO 게시판 조회

    // TODO 게시판 삭제

    // TODO 센터로 게시판 조회

    // TODO 모두의 이야기 게시판 조회

    // TODO 이름으로 게시판 조회

    // TODO Default 게시판 조회

    // TODO 모두의이야기 default 게시판 조회

    // TODO find By Center
}