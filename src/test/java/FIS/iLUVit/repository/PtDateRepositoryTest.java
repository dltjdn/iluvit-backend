package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class PtDateRepositoryTest {

    Center center1;
    Center center2;
    Presentation presentation;
    PtDate ptDate1;
    PtDate ptDate2;

    @Autowired
    PtDateRepository ptDateRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void init(){
        center1 = Center.builder()
                .name("test 유치원")
                .build();

        presentation = Presentation.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .place("테스트 장소")
                .content("테스트 설명회")
                .videoCnt(1)
                .center(center1)
                .build();

        center2 = Center.builder()
                .address("test 주소")
                .name("test 유치원")
                .build();

        ptDate1 = PtDate.builder()
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation)
                .build();

        ptDate2 = PtDate.builder()
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation)
                .build();

        Parent parent = Parent.builder()
                .auth(Auth.PARENT)
                .name("test")
                .build();

        Participation participation = Participation.builder()
                .ptDate(ptDate1)
                .parent(parent)
                .status(Status.JOINED)
                .build();


    }

    @Test
    @Transactional
    public void 설명회_회차_정보_가져오기() throws Exception {
        //given init으로 대체 setup
        em.persist(center1);
        em.persist(presentation);
        em.persist(ptDate1);
        em.flush();
        em.clear();

        //when
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDate1.getId()).orElse(null);

        //then
        assertThat(ptDate).isEqualTo(ptDate1);
        assertThat(ptDate.getPresentation().getId()).isEqualTo(presentation.getId());
        assertThat(ptDate.getPresentation().getCenter().getId()).isEqualTo(center1.getId());
    }

    @Test
    public void 학부모_정보_조회() throws Exception {
        //given

        //when

        //then
    }

}