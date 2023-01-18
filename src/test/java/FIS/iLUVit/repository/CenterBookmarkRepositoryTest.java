package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class CenterBookmarkRepositoryTest {

    @Autowired
    private CenterBookmarkRepository centerBookmarkRepository;

    @Autowired
    private EntityManager em;

    private Center center1;
    private Center center2;
    private Parent parent1;
    private Parent parent2;
    private Prefer prefer1;

    @BeforeEach
    public void init() {
        center1 = Creator.createCenter("center1");
        center2 = Creator.createCenter("center2");
        parent1 = Creator.createParent("parent1", "phoneNum1");
        parent2 = Creator.createParent("parent2", "phoneNum2");
        prefer1 = Creator.createPrefer(parent1, center1);
        em.persist(center1);
        em.persist(center2);
        em.persist(parent1);
        em.persist(parent2);
        em.persist(prefer1);
    }

    @Test
    public void findByUserIdAndCenterId() {
        // given
        em.flush();
        em.clear();
        // when
        Prefer result = centerBookmarkRepository.findByUserIdAndCenterId(parent1.getId(), center1.getId())
                .orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getCenter().getId()).isEqualTo(center1.getId());
        assertThat(result.getParent().getId()).isEqualTo(parent1.getId());
    }

}
