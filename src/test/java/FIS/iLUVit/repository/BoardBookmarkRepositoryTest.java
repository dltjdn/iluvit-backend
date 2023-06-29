package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class BoardBookmarkRepositoryTest {

    // TODO 모두의_이야기_북마크_조회

    // TODO 센터의_이야기_북마크_조회

    // TODO 북마크별_최신_게시글_하나씩_조회

    // TODO 북마크_삭제_게시판과_유저로

    // TODO delete All By Board And User

    // TODO find By User With Board

    // TODO delete All By Center And User

}