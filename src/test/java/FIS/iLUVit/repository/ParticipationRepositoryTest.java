package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParticipationRepositoryTest {

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("설명회 신청 관련")
    class doParticipation{
        @Test
        public void 설명회_신청_취소를_위한_데이터_조회() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanRegisterPtDate(presentation);
            Parent parent = createParent();
            Participation participation = createJoinParticipation(ptDate, parent);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(participation);
            em.flush();
            //when
            Optional<Participation> result = participationRepository.findByIdAndStatusWithPtDate(participation.getId(), parent.getId());

            //then
            assertThat(result.get().getId()).isEqualTo(participation.getId());
            assertThat(result.get().getStatus()).isEqualTo(Status.JOINED);
        }

        @Test
        public void 설명회_신청_취소를_위한_데이터_조회_결과_없음() throws Exception {
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
            Optional<Participation> result = participationRepository.findByIdAndStatusWithPtDate(participation.getId(), parent.getId());

            //then
            assertThat(result.orElse(null)).isNull();
        }

        @Test
        public void 설명회_신청_취소_잘못된_학부모_아이디() throws Exception {
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
            Optional<Participation> result = participationRepository.findByIdAndStatusWithPtDate(participation.getId(), 1000L);

            //then
            assertThat(result.orElse(null)).isNull();
        }

        @Test
        public void 신청_조회_JOINED되는_것만_출력() throws Exception {
            //given
            Center center = createCenter("test", true, true, null);
            Parent parent = createParent();
            Presentation presentation = createValidPresentation(center);
            PtDate ptDate = createCanNotRegisterPtDate(presentation);
            Participation joinParticipation = createJoinParticipation(ptDate, parent);
            Participation cancelParticipation = createCancelParticipation(ptDate, parent);
            em.persist(center);
            em.persist(presentation);
            em.persist(ptDate);
            em.persist(parent);
            em.persist(joinParticipation);
            em.persist(cancelParticipation);
            em.flush();

            //when
            List<Participation> target = participationRepository.findByPtDateAndStatusJOINED(ptDate.getId());

            //then
            assertThat(target.size()).isEqualTo(1);
            assertThat(target.contains(cancelParticipation)).isFalse();

        }
    }
}