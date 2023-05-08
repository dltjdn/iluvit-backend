package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ScrapRepositoryTest {

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private EntityManager em;

    private Parent parent1;
    private Parent parent2;
    private Parent parent3;
    private Post post1;
    private Post post2;
    private Post post3;
    private Scrap scrap1;
    private Scrap scrap2;
    private Scrap scrap3;
    private ScrapPost scrapPost1;
    private ScrapPost scrapPost2;

    @BeforeEach
    public void init() {
        parent1 = Creator.createParent(null, "parent1", "parent1", "parent1");
        parent2 = Creator.createParent(null, "parent2", "parent2", "parent2");
        parent3 = Creator.createParent(null, "parent3", "parent3", "parent3");
        post1 = Creator.createPost("post1", "post1", true, null, parent1);
        post2 = Creator.createPost("post2", "post2", true, null, parent1);
        post3 = Creator.createPost("post2", "post2", true, null, parent2);
        scrap1 = Scrap.createDefaultScrap(parent1);
        scrap2 = Scrap.createScrap(parent1, "scrap2");
        scrap3 = Scrap.createScrap(parent2, "scrap3");
        scrapPost1 = ScrapPost.createScrapPost(post1, scrap1);
        scrapPost2 = ScrapPost.createScrapPost(post3, scrap1);
        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.persist(scrap1);
        em.persist(scrap2);
        em.persist(scrap3);
        em.persist(scrapPost1);
        em.persist(scrapPost2);
    }

    @Nested
    @DisplayName("findScrapsByUserWithScrapPosts")
    class findScrapsByUserWithScrapPosts {

        @Test
        public void scrapPost가있는경우() {
            // given
            em.flush();
            em.clear();
            // when
            List<Scrap> result = scrapRepository.findScrapsByUserWithScrapPosts(parent1.getId());
            // then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).getUser().getId()).isEqualTo(parent1.getId());
            for (Scrap scrap : result) {
                if (!scrap.getScrapPosts().isEmpty()) {
                    assertThat(scrap.getScrapPosts().size()).isEqualTo(2);
                }
            }
        }

        @Test
        public void scrapPost가없는경우() {
            // given
            em.flush();
            em.clear();
            // when
            List<Scrap> result = scrapRepository.findScrapsByUserWithScrapPosts(parent3.getId());
            // then
            assertThat(result.size()).isEqualTo(0);
        }

    }

    @Nested
    @DisplayName("findScrapByIdAndUserId")
    class findScrapByIdAndUserId{
        @Test
        public void 결과가있을경우() {
            // given
            em.flush();
            em.clear();
            // when
            Scrap result = scrapRepository.findScrapByIdAndUserId(scrap1.getId(), parent1.getId()).orElse(null);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(scrap1.getId());
        }
        @Test
        public void 결과가없는경우() {
            // given
            em.flush();
            em.clear();
            // when
            Scrap result = scrapRepository.findScrapByIdAndUserId(-1L, parent1.getId()).orElse(null);
            // then
            assertThat(result).isNull();
        }
    }





}
