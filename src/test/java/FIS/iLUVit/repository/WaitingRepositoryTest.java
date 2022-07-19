package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import static FIS.iLUVit.Creator.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class WaitingRepositoryTest {

    @Autowired
    WaitingRepository waitingRepository;

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
            Waiting waiting1 = createWaiting(ptDate, parent, 0);
            Waiting waiting2 = createWaiting(ptDate, parent, 1);
            Waiting waiting3 = createWaiting(ptDate, parent, 2);
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
            waitingRepository.findMinWaitingOrder()

            //then
        }
    }

}