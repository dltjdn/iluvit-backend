package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class PtDateRepositoryTest {

    // TODO 설명회_회차_정보_가져오기

    // TODO 학부모_정보_조회

    @Nested
    @DisplayName("설명회_신청")
    class DoParticipation {

        // TODO 설명회_신청_설명회_회차_정보_가져오기
    }

    @Nested
    @DisplayName("설명회_대기_신청")
    class DoWaiting {

        // TODO 대기자_등록_ptDate_가져오기

        // TODO 대기자_등록_ptDate_가져오기2
    }

}