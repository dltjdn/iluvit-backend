package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.dto.parent.ParticipationListDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import java.io.IOException;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EntityManager em;

    private Parent parent1;
    private Parent parent2;
    private Parent parent3;
    private Center center1;
    private Center center2;
    private Child child1;
    private Child child2;
    private Child child3;
    private Child child4;
    private Prefer prefer1;
    private Prefer prefer2;
    @BeforeEach
    public void init() throws IOException {
        parent1 = Creator.createParent();
        parent2 = Creator.createParent();
        parent3 = Creator.createParent();
        center1 = Creator.createCenter("center1");
        center2 = Creator.createCenter("center2");
        child1 = Creator.createChild("child1", parent1, center1, Approval.ACCEPT);
        child2 = Creator.createChild("child2", parent1, center1, Approval.ACCEPT);
        child3 = Creator.createChild("child3", parent1, center2, Approval.WAITING);
        child4 = Creator.createChild("child4", parent2, null, Approval.WAITING);
        prefer1 = Creator.createPrefer(parent1, center1);
        prefer2 = Creator.createPrefer(parent2, center2);
    }

    @Nested
    @DisplayName("findWithChildren")
    class findWithChildren{
        @Test
        public void 아이가없는경우() {
            // given
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);
            em.persist(center1);
            em.persist(center2);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);
            em.persist(child4);
            em.flush();
            em.clear();
            // when
            Parent result = parentRepository.findWithChildren(parent3.getId()).get();
            // then
            assertThat(result.getChildren().size()).isEqualTo(0);
        }
        @Test
        public void 아이가있는경우() {
            // given
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);
            em.persist(center1);
            em.persist(center2);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);
            em.persist(child4);
            em.flush();
            em.clear();
            // when
            Parent result = parentRepository.findWithChildren(parent1.getId()).get();
            // then
            assertThat(result.getChildren().size()).isEqualTo(3);
        }
        @Test
        public void 시설없는아이한명() {
            // given
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);
            em.persist(center1);
            em.persist(center2);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);
            em.persist(child4);
            em.flush();
            em.clear();
            // when
            Parent result = parentRepository.findWithChildren(parent2.getId()).get();
            // then
            assertThat(result.getChildren().size()).isEqualTo(1);
        }
    }


    @Nested
    @DisplayName("자신이 신청 - 취소 - 대기한 설명회 목록 가져오기 ")
    class 자신이신청취소대기한설명회목록{


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
            Slice<ParticipationListDto> result = parentRepository.findMyJoinParticipation(parent.getId(), PageRequest.of(0, 2));

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
            Slice<ParticipationListDto> result = parentRepository.findMyCancelParticipation(parent.getId(), PageRequest.of(0, 2));

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
            Slice<ParticipationListDto> result = parentRepository.findMyWaiting(parent.getId(), PageRequest.of(0, 2));

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.hasNext()).isFalse();
        }
    }

    @Test
    public void findByIdWithChild() {
        // given
        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(center1);
        em.persist(center2);
        em.persist(child1);
        em.persist(child2);
        em.persist(child3);
        em.persist(child4);
        em.flush();
        em.clear();
        // when
        Parent result = parentRepository.findByIdWithChild(parent1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getChildren().size()).isEqualTo(3);
        for (Child child : result.getChildren()) {
            assertThat(child.getParent().getId()).isEqualTo(parent1.getId());
        }
    }

    @Test
    public void findByIdWithPreferWithCenter() {
        // given
        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(center1);
        em.persist(center2);
        em.persist(child1);
        em.persist(child2);
        em.persist(child3);
        em.persist(child4);
        em.persist(prefer1);
        em.persist(prefer2);
        em.flush();
        em.clear();
        // when
        Parent result = parentRepository.findByIdWithPreferWithCenter(parent1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getPrefers().size()).isEqualTo(1);
        assertThat(result.getPrefers().get(0).getCenter().getId()).isEqualTo(center1.getId());
    }
}