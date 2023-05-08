package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReviewHeartRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    ReviewHeartRepository reviewHeartRepository;

    Parent parent1;
    Parent parent2;
    Parent parent3;

    Teacher teacher1;

    Center center1;

    Review review1;
    Review review2;
    Review review3;

    ReviewHeart reviewHeart1;
    ReviewHeart reviewHeart2;
    ReviewHeart reviewHeart3;

    @BeforeEach
    public void init() {
        parent1 = Parent.builder()
                .name("ParentA")
                .auth(Auth.PARENT)
                .build();
        parent2 = Parent.builder()
                .name("ParentB")
                .auth(Auth.PARENT)
                .build();
        parent3 = Parent.builder()
                .name("ParentC")
                .auth(Auth.PARENT)
                .build();
        teacher1 = Teacher.builder()
                .name("TeacherA")
                .auth(Auth.DIRECTOR)
                .build();

        center1 = Creator.createCenter("팡팡유치원", true, true, null);
        review1 = Creator.createReview(center1, 5, parent1, null, "위생에 철저해요");
        review1.updateAnswer("리뷰 남겨주셔서 감사합니다", teacher1);
        review2 = Creator.createReview(center1, 4, parent2, null, "나쁘지 않아요");
        review3 = Creator.createReview(center1, 1, parent3, null, "불친절해요");

        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(teacher1);
        em.persist(center1);
        em.persist(review1);
        em.persist(review2);
        em.persist(review3);
        em.flush();
        em.clear();

        reviewHeart1 = new ReviewHeart(review1, parent1);
        reviewHeart2 = new ReviewHeart(review1, parent2);
        reviewHeart3 = new ReviewHeart(review1, parent3);
    }

    @Test
    public void 좋아요_저장() throws Exception {
        //given
        reviewHeartRepository.save(reviewHeart1);
        reviewHeartRepository.save(reviewHeart2);
        reviewHeartRepository.save(reviewHeart3);

        //when
        List<ReviewHeart> all = reviewHeartRepository.findAll();
        //then
        assertThat(all)
                .extracting("user")
                .containsOnly(parent1, parent2, parent3);
    }

    @Test
    public void 리뷰와_유저_아이디로_좋아요_찾기() throws Exception {
        //given
        reviewHeartRepository.save(reviewHeart1);
        reviewHeartRepository.save(reviewHeart2);
        reviewHeartRepository.save(reviewHeart3);
        //when
        ReviewHeart reviewHeart = reviewHeartRepository.findByReviewAndUser(review1.getId(), parent1.getId())
                .orElse(null);
        //then
        assertThat(reviewHeart.getReview())
                .isEqualTo(review1);

        assertThat(reviewHeart.getUser().getId())
                .isEqualTo(parent1.getId());

        assertThat(reviewHeart)
                .isEqualTo(reviewHeart1);

    }
}