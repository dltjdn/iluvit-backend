package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.enumtype.BoardKind;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.ScrapPostRepository;
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

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ScrapPostRepositoryTest {

    @Autowired
    private ScrapPostRepository scrapPostRepository;

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
    private ScrapPost scrapPost3;
    private Center center1;
    private Center center2;
    private Board board1;
    private Board board2;
    private Board board3;

    @BeforeEach
    public void init() {
        parent1 = Creator.createParent(null, "parent1", "parent1", "parent1");
        parent2 = Creator.createParent(null, "parent2", "parent2", "parent2");
        parent3 = Creator.createParent(null, "parent3", "parent3", "parent3");
        center1 = Center.builder().name("center1").build();
        center2 = Center.builder().name("center2").build();
        board1 = Board.createBoard("board1", BoardKind.NOTICE, null, true);
        board2 = Board.createBoard("board2", BoardKind.NOTICE, center1, true);
        board3 = Board.createBoard("board3", BoardKind.NOTICE, center2, false);
        post1 = Creator.createPost("post1", "post1", true, board1, parent1);
        post2 = Creator.createPost("post2", "post2", true, board1, parent1);
        post3 = Creator.createPost("post2", "post2", true, board2, parent2);
        scrap1 = Scrap.createDefaultScrap(parent1);
        scrap2 = Scrap.createScrap(parent1, "scrap2");
        scrap3 = Scrap.createScrap(parent2, "scrap3");
        scrapPost1 = ScrapPost.createScrapPost(post1, scrap1);
        scrapPost2 = ScrapPost.createScrapPost(post3, scrap1);
        scrapPost3 = ScrapPost.createScrapPost(post2, scrap3);
        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(center1);
        em.persist(center2);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.persist(scrap1);
        em.persist(scrap2);
        em.persist(scrap3);
        em.persist(scrapPost1);
        em.persist(scrapPost2);
        em.persist(scrapPost3);
    }

    @Nested
    @DisplayName("findByScrapAndPost")
    class findByScrapAndPost{

        @Test
        public void 요청이정상적인경우() {
            // given
            em.flush();
            em.clear();
            // when
            ScrapPost result = scrapPostRepository.findByScrapAndPost(parent1.getId(), scrapPost1.getId()).orElse(null);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(scrapPost1.getId());
        }

        @Test
        public void scrapPostId오류() {
            // given
            em.flush();
            em.clear();
            // when
            ScrapPost result = scrapPostRepository.findByScrapAndPost(parent1.getId(), scrapPost3.getId()).orElse(null);
            // then
            assertThat(result).isNull();
        }
    }

    @Test
    public void findByScrapWithPost() {
        // given
        em.flush();
        em.clear();
        // when
        Slice<ScrapPost> result = scrapPostRepository.findByScrapWithPost(parent1.getId(), scrap1.getId(), PageRequest.of(0, 5));
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        result.getContent().forEach(sp -> {
            if (Objects.equals(sp.getId(), scrapPost1.getId())) {
                assertThat(sp.getScrap().getId()).isEqualTo(scrap1.getId());
                assertThat(sp.getPost().getId()).isEqualTo(post1.getId());
                assertThat(sp.getPost().getUser().getId()).isEqualTo(parent1.getId());
                assertThat(sp.getPost().getBoard().getCenter()).isNull();
            } else if (Objects.equals(sp.getId(), scrapPost2.getId())) {
                assertThat(sp.getScrap().getId()).isEqualTo(scrap1.getId());
                assertThat(sp.getPost().getId()).isEqualTo(post3.getId());
                assertThat(sp.getPost().getUser().getId()).isEqualTo(parent2.getId());
                assertThat(sp.getPost().getBoard().getCenter().getId()).isEqualTo(center1.getId());
            }
        });

    }
}
