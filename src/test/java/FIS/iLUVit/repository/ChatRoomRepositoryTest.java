package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ChatRoomRepositoryTest {

    // TODO 내정보_상대방정보_게시글로_채팅방찾기

    // TODO 내정보로_채팅방찾기

    // TODO Post_null값으로_업데이트

    // TODO 내정보_상대방정보_게시글_익명정보로_채팅방찾기
}