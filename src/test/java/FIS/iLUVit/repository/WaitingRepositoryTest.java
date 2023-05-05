package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.PtDateRepository;
import FIS.iLUVit.repository.iluvit.WaitingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import java.util.Optional;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class WaitingRepositoryTest {

    @Autowired
    WaitingRepository waitingRepository;

    @Autowired
    PtDateRepository ptDateRepository;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("설명회_취소_관련")
    class doParticipate {

        @Test
        public void 가장_높은_대기순번_찾기() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            Waiting waiting1 = createWaiting(ptDate, parent, 1);
            Waiting waiting2 = createWaiting(ptDate, parent, 2);
            Waiting waiting3 = createWaiting(ptDate, parent, 3);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.persist(waiting1);
            em.persist(waiting2);
            em.persist(waiting3);
            em.flush();

            //when
            Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate);

            //then
            assertThat(waiting.getWaitingOrder()).isEqualTo(1);
            assertThat(waiting.getId()).isEqualTo(waiting1.getId());

        }

        @Test
        public void 가장_높은_대기순번_찾기2() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            Waiting waiting1 = createWaiting(ptDate, parent, 5);
            Waiting waiting2 = createWaiting(ptDate, parent, 2);
            Waiting waiting3 = createWaiting(ptDate, parent, 3);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.persist(waiting1);
            em.persist(waiting2);
            em.persist(waiting3);
            em.flush();

            //when
            Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate);

            //then
            assertThat(waiting.getWaitingOrder()).isEqualTo(2);
            assertThat(waiting.getId()).isEqualTo(waiting2.getId());

        }

        @Test
        public void 가장_높은_대기순번_찾기3() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.flush();

            //when
            Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate);

            //then
            assertThat(waiting).isNull();

        }

        @Test
        public void 설명회_대기자_신청자로_전환_WAITINGORDER_일괄_업데이트() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createCancelParticipation(ptDate, parent);
            Waiting waiting1 = createWaiting(ptDate, parent, 5);
            Waiting waiting2 = createWaiting(ptDate, parent, 2);
            Waiting waiting3 = createWaiting(ptDate, parent, 3);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.persist(waiting1);
            em.persist(waiting2);
            em.persist(waiting3);
            em.flush();
            //when
            Waiting waiting = waitingRepository.findMinWaitingOrder(ptDate);
            waitingRepository.updateWaitingOrderForPtDateChange(waiting.getWaitingOrder(), ptDate);
            waitingRepository.delete(waiting);
            em.flush();
            em.clear();

            Optional<Waiting> deleteWaiting = waitingRepository.findById(waiting.getId());
            PtDate updatedPtDate = ptDateRepository.findByIdWithWaitingAndPresentationAndCenterAndParent(ptDate.getId()).get();

            //then
            assertThat(deleteWaiting.orElse(null)).isNull();
            assertThat(updatedPtDate.getWaitings().get(0).getWaitingOrder()).isEqualTo(3);
            assertThat(updatedPtDate.getWaitings().get(1).getWaitingOrder()).isEqualTo(1);


        }

        @Test
        @DisplayName("[success] 대기정보 가져오기 성공")
        public void 대기정보가져오기() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Parent parent1 = createParent();
            Parent parent2 = createParent();
            Parent parent3 = createParent();
            Waiting waiting1 = createWaiting(ptDate, parent1, 1);
            Waiting waiting2 = createWaiting(ptDate, parent2, 2);
            Waiting waiting3 = createWaiting(ptDate, parent3, 3);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);
            em.persist(waiting1);
            em.persist(waiting2);
            em.persist(waiting3);
            em.flush();
            em.clear();
            //when

            Waiting target = waitingRepository.findByIdWithPtDate(waiting1.getId(), parent1.getId()).get();
            Waiting target2 = waitingRepository.findByIdWithPtDate(waiting1.getId(), parent2.getId()).orElse(null);
            //then

            assertThat(target.getId()).isEqualTo(waiting1.getId());
            assertThat(target.getPtDate().getId()).isEqualTo(ptDate.getId());
            assertThat(target2).isNull();
        }

        @Test
        @DisplayName("[success] 대기자 일괄 WaitingOrder 감소")
        public void 대기자WaitingOrder감소() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate1 = createCanNotRegisterPtDate(presentation);
            PtDate ptDate2 = createCanNotRegisterPtDate(presentation);
            Parent parent1 = createParent();
            Parent parent2 = createParent();
            Parent parent3 = createParent();
            Waiting waiting1 = createWaiting(ptDate1, parent1, 1);
            Waiting waiting2 = createWaiting(ptDate1, parent2, 2);
            Waiting waiting3 = createWaiting(ptDate1, parent3, 3);
            Waiting waiting4 = createWaiting(ptDate2, parent1, 4);
            Waiting waiting5 = createWaiting(ptDate2, parent2, 2);
            Waiting waiting6 = createWaiting(ptDate2, parent3, 3);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate1);
            em.persist(ptDate2);
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);
            em.persist(waiting1);
            em.persist(waiting2);
            em.persist(waiting3);
            em.persist(waiting4);
            em.persist(waiting5);
            em.persist(waiting6);
            em.flush();
            em.clear();
            //when
            waitingRepository.delete(waiting2);
            waitingRepository.updateWaitingOrder(ptDate1, 2);
            Waiting result1 = waitingRepository.findById(waiting3.getId()).get();
            Waiting result2 = waitingRepository.findById(waiting1.getId()).get();
            Waiting result3 = waitingRepository.findById(waiting2.getId()).orElse(null);
            Waiting result4 = waitingRepository.findById(waiting4.getId()).get();
            //then

            assertThat(result1.getWaitingOrder()).isEqualTo(2);
            assertThat(result2.getWaitingOrder()).isEqualTo(1);
            assertThat(result4.getWaitingOrder()).isEqualTo(4);
            assertThat(result3).isNull();

        }

    }

}