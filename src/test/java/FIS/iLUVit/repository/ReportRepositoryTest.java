package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.iluvit.Post;
import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.domain.iluvit.reports.Report;
import FIS.iLUVit.repository.iluvit.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private EntityManager em;

    Teacher teacher1;
    Post post1;
    Report report1;

    @BeforeEach
    public void init(){
        teacher1 = Creator.createTeacher("name", null, Approval.ACCEPT);
        post1 = Creator.createPost("title", "content", false,null, teacher1);
        report1 = Creator.createReport(post1.getId(), teacher1);
        em.persist(teacher1);
        em.persist(post1);
        em.persist(report1);
        em.flush();
        em.clear();
    }

    @Test
    public void 타겟아이디로조회(){
        //given

        //when
        Report report = reportRepository.findByTargetId(report1.getTargetId()).orElse(null);
        //then
        assertThat(report).isNotNull();
        assertThat(report.getTargetId()).isEqualTo(report1.getTargetId());
    }

}