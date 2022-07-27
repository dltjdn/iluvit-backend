package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParentRepositoryTest {



    @Nested
    @DisplayName("자신이 신청 - 취소 - 대기한 설명회 목록 가져오기 ")
    class 자신이신청취소대기한설명회목록{

        @Autowired
        ParentRepository parentRepository;

        @Autowired
        EntityManager em;

        @Test
        @DisplayName("[success] 자신이 신청한 설명회 가져오기")
        public void 학부모가신청한설명회() throws Exception {
            //given
            Kindergarten center = createKindergarten("test");
            Parent parent = createParent();
            Presentation invalidPresentation = createInvalidPresentation(center);
            Presentation validPresentation = createValidPresentation(center);
            PtDate canRegisterPtDate = createCanRegisterPtDate(validPresentation);
            PtDate canNotRegisterPtDate = createCanNotRegisterPtDate(invalidPresentation);
            Waiting waiting1 = createWaiting(canRegisterPtDate, parent, 1);
            Participation cancelParticipation = createCancelParticipation(canNotRegisterPtDate, parent);
            Participation joinParticipation = createJoinParticipation(canRegisterPtDate, parent);

            em.persist(center);
            em.persist(parent);
            em.persist(invalidPresentation);
            em.persist(validPresentation);
            em.persist(canRegisterPtDate);
            em.persist(canNotRegisterPtDate);
            em.persist(waiting1);
            em.persist(cancelParticipation);
            em.persist(joinParticipation);
            em.flush();
            em.clear();

            //when
            Slice<MyParticipationsDto> result = parentRepository.findMyJoinParticipation(parent.getId(), PageRequest.of(0, 2));

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.hasNext()).isFalse();

        }

        @Test
        @DisplayName("[success] 자신이 취소한 설명회 가져오기")
        public void 학부모가취소한설명회() throws Exception {
            //given
            Kindergarten center = createKindergarten("test");
            Parent parent = createParent();
            Presentation invalidPresentation = createInvalidPresentation(center);
            Presentation validPresentation = createValidPresentation(center);
            PtDate canRegisterPtDate = createCanRegisterPtDate(validPresentation);
            PtDate canNotRegisterPtDate = createCanNotRegisterPtDate(invalidPresentation);
            Waiting waiting1 = createWaiting(canRegisterPtDate, parent, 1);
            Participation cancelParticipation = createCancelParticipation(canNotRegisterPtDate, parent);
            Participation joinParticipation = createJoinParticipation(canRegisterPtDate, parent);

            em.persist(center);
            em.persist(parent);
            em.persist(invalidPresentation);
            em.persist(validPresentation);
            em.persist(canRegisterPtDate);
            em.persist(canNotRegisterPtDate);
            em.persist(waiting1);
            em.persist(cancelParticipation);
            em.persist(joinParticipation);
            em.flush();
            em.clear();

            //when
            Slice<MyParticipationsDto> result = parentRepository.findMyCancelParticipation(parent.getId(), PageRequest.of(0, 2));

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("[success] 자신이 대기중인 설명회 가져오기")
        public void 대기신청설명회() throws Exception {
            //given
            Kindergarten center = createKindergarten("test");
            Parent parent = createParent();
            Presentation invalidPresentation = createInvalidPresentation(center);
            Presentation validPresentation = createValidPresentation(center);
            PtDate canRegisterPtDate = createCanRegisterPtDate(validPresentation);
            PtDate canNotRegisterPtDate = createCanNotRegisterPtDate(invalidPresentation);
            Waiting waiting1 = createWaiting(canRegisterPtDate, parent, 1);
            Participation cancelParticipation = createCancelParticipation(canNotRegisterPtDate, parent);
            Participation joinParticipation = createJoinParticipation(canRegisterPtDate, parent);

            em.persist(center);
            em.persist(parent);
            em.persist(invalidPresentation);
            em.persist(validPresentation);
            em.persist(canRegisterPtDate);
            em.persist(canNotRegisterPtDate);
            em.persist(waiting1);
            em.persist(cancelParticipation);
            em.persist(joinParticipation);
            em.flush();
            em.clear();
            //when
            Slice<MyParticipationsDto> result = parentRepository.findMyWaiting(parent.getId(), PageRequest.of(0, 2));

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.hasNext()).isFalse();
        }
    }
}