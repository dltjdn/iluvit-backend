package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ChatRepositoryTest {

    // TODO 나의_채팅방내_채팅목록
}