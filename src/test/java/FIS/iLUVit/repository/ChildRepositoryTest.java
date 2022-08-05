package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Approval;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ChildRepositoryTest {
    
    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private EntityManager em;

    private Center center1;
    private Center center2;
    private Parent parent1;
    private Parent parent2;
    private Child child1;
    private Child child2;
    private Child child3;
    private Child child4;
    
    @BeforeEach
    public void init() {
        center1 = Creator.createCenter("center1", true, Creator.createArea("서울시", "구로구"));
        center2 = Creator.createCenter("center2", false, Creator.createArea("서울시", "구로구"));
        parent1 = Creator.createParent("parent1", "parent1");
        parent2 = Creator.createParent("parent2", "parent2");
        child1 = Creator.createChild("child1", parent1, center1, Approval.ACCEPT);
        child2 = Creator.createChild("child2", parent1, center1, Approval.WAITING);
        child3 = Creator.createChild("child3", parent1, center2, Approval.WAITING);
        child4 = Creator.createChild("child4", parent2, null, Approval.WAITING);
        em.persist(center1);
        em.persist(center2);
        em.persist(parent1);
        em.persist(parent2);
        em.persist(child1);
        em.persist(child2);
        em.persist(child3);
        em.persist(child4);
    }
    
    @Nested
    @DisplayName("findByIdWithParentAndCenter")
    class findByWithParentAndCenter{
        @Test
        public void 정상요청() {
            // given
            em.flush();
            em.clear();
            // when
            Child result = childRepository.findByIdWithParentAndCenter(parent1.getId(), child1.getId()).orElse(null);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(child1.getId());
            assertThat(result.getCenter().getName()).isEqualTo(center1.getName());
        }

        @Test
        public void 잘못된요청() {
            // given
            em.flush();
            em.clear();
            // when
            Child result = childRepository.findByIdWithParentAndCenter(parent1.getId(), child4.getId()).orElse(null);
            // then
            assertThat(result).isNull();
        }
    }

    @Test
    public void findByUserWithCenter() {
        // given
        em.flush();
        em.clear();
        // when
        List<Child> result = childRepository.findByUserWithCenter(parent1.getId());
        // then
        assertThat(result.size()).isEqualTo(3);
        for (Child child : result) {
            assertThat(child.getParent().getId()).isEqualTo(parent1.getId());
        }
    }
}
