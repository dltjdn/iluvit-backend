package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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