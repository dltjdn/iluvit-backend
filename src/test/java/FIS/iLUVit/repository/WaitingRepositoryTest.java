package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class WaitingRepositoryTest {

    @Nested
    @DisplayName("설명회_취소_관련")
    class doParticipate {

        // TODO 가장_높은_대기순번_찾기

        // TODO 가장_높은_대기순번_찾기2

        // TODO 가장_높은_대기순번_찾기3

        // TODO 설명회_대기자_신청자로_전환_WAITINGORDER_일괄_업데이트

        // TODO 대기정보 가져오기 성공

        // TODO 대기자 일괄 WaitingOrder 감소
    }
}