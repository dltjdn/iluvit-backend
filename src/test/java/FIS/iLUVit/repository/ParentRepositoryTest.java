package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.controller.dto.MyParticipationsDto;
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
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private MockMultipartFile multipartFile;
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
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
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