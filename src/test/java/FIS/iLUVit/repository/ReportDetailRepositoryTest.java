package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReportDetailRepositoryTest {

    // TODO 신고상세내역조회_유저아이디_포스트아이디

    // TODO 신고상세내역조회_유저아이디_댓글아이디
}