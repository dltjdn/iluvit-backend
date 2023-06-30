package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class PresentationRepositoryTest {

    @Nested
    @DisplayName("설명회_필터_검색")
    class FindByFilter {

        // TODO 설명회_검색_결과_없음

        // TODO 설명회_검색_결과_없음2
    }

    @Nested
    @DisplayName("시설 상세보기에서 설명회 버튼 눌렀을 때 조회 될 내용")
    class 설명회버튼조회내용 {

        // TODO 학부모의 시설 설명회 상세보기 로그인 X

        // TODO 부학부모 시설 상세보기 로그인 O
    }
}