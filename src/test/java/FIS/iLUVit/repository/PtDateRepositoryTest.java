package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.Status;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.PtDateRepository;
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

    @Nested
    @DisplayName("설명회_신청")
    class DoParticipation {

//        @Test
//        public void 설명회_신청_설명회_회차_정보_가져오기() throws Exception {
//            //given
//            Center center = createCenter("test", true, true, null);
//            Presentation presentation = createInvalidPresentation(center);
//            PtDate ptDate = Creator.createCanRegisterPtDate(presentation);
//
//            //when
//            ptDateRepository.findByIdAndPresentationDate();
//
//            //then
//        }
    }

    @Nested
    @DisplayName("설명회_대기_신청")
    class DoWaiting {

        @Test
        public void 대기자_등록_ptDate_가져오기() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.flush();

            //when
            PtDate target = ptDateRepository.findByIdWith(ptDate.getId()).get();

            //then
            assertThat(target.getId()).isEqualTo(ptDate.getId());
            assertThat(target.getPresentation().getId()).isEqualTo(presentation.getId());
        }

        @Test
        public void 대기자_등록_ptDate_가져오기2() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Parent parent1 = createParent();
            Parent parent2 = createParent();
            Participation participation = createCancelParticipation(ptDate, parent1);
            Waiting waiting1 = createWaiting(ptDate, parent1, 1);
            Waiting waiting2 = createWaiting(ptDate, parent2, 2);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent1);
            em.persist(parent2);
            em.persist(participation);
            em.persist(waiting1);
            em.persist(waiting2);
            em.flush();
            em.clear();

            //when
            PtDate target = ptDateRepository.findByIdWith(ptDate.getId()).get();

            //then
            assertThat(target.getId()).isEqualTo(ptDate.getId());
            assertThat(target.getPresentation().getId()).isEqualTo(presentation.getId());
            assertThat(target.getWaitings().size()).isEqualTo(2);

        }
    }

}