package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParticipationRepositoryTest {
    @Nested
    @DisplayName("설명회 신청 관련")
    class doParticipation{

        // TODO 설명회_신청_취소를_위한_데이터_조회

        // TODO 설명회_신청_취소를_위한_데이터_조회_결과_없음

        // TODO 설명회_신청_취소_잘못된_학부모_아이디

        // TODO 신청_조회_JOINED되는_것만_출력
    }
}