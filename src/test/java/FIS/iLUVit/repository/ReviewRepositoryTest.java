package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Auth;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReviewRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    ReviewRepository reviewRepository;

    Parent parent1;
    Parent parent2;
    Parent parent3;

    Teacher teacher1;

    Center center1;

    Review review1;
    Review review2;
    Review review3;

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
    }

    @Test
    public void 학부모_아이디로_리뷰_조회() throws Exception {
        //given

        //when
        Slice<Review> reviewList = reviewRepository.findByParent(parent1.getId(), PageRequest.of(0, 10));
        //then
        assertThat(reviewList)
                .extracting("score", "teacher", "content")
                .containsOnly(
                        Tuple.tuple(5, teacher1, "위생에 철저해요")
                );
    }

    @Test
    public void 센터_아이디로_리뷰_조회() throws Exception {
        //given

        //when
        Slice<Review> reviewList = reviewRepository
                .findByCenterId(center1.getId(), PageRequest.of(0, 10));

        //then
        List<Review> content = reviewList.getContent();

        assertThat(reviewList)
                .extracting("score", "teacher", "content")
                .containsOnly(
                        Tuple.tuple(5, teacher1, "위생에 철저해요"),
                        Tuple.tuple(4, null, "나쁘지 않아요"),
                        Tuple.tuple(1, null, "불친절해요")
                );
    }

    @Test
    public void 센터와_학부모로_리뷰_조회() throws Exception {
        //given

        //when
        Review findReview = reviewRepository
                .findByUserAndCenter(parent1.getId(), center1.getId())
                .orElse(null);

        //then

        assertThat(findReview.getScore())
                .isEqualTo(5);
        assertThat(findReview.getTeacher())
                .isNotNull();
        assertThat(findReview.getContent())
                .isEqualTo("위생에 철저해요");
    }
}