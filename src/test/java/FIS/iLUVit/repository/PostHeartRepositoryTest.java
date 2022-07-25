package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class PostHeartRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    PostHeartRepository postHeartRepository;

    ObjectMapper objectMapper;

    Center center1;
    Center center2;
    Center center3;

    Long centerId1;
    Long centerId2;
    Long centerId3;

    Board board1;
    Board board2;
    Board board3;
    Board board4;
    Board board5;

    Post post1;
    Post post2;
    Post post3;
    Post post4;
    Post post5;
    Post post6;
    Post post7;
    Post post8;
    Post post9;
    Post post10;
    Post post11;
    Post post12;
    Post post13;

    Child child1;
    Child child2;
    Child child3;

    Parent parent1;
    Parent parent2;
    Parent parent3;

    Teacher teacher1;
    Teacher teacher2;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        center1 = Creator.createCenter("팡팡유치원", true, true, null);
        center2 = Creator.createCenter("가산유치원", true, true, null);
        center3 = Creator.createCenter("디지털유치원", true, true, null);

        parent1 = Parent.builder()
                .name("ParentA")
                .auth(Auth.PARENT)
                .build();
        parent1.getChildren().add(child1);
        parent1.getChildren().add(child2);
        parent1.getChildren().add(child3);

        child1 = Child.createChild("childA", null, null, parent1);
        child2 = Child.createChild("childB", null, null, parent1);
        child3 = Child.createChild("childC", null, null, parent1);
        child1.mappingCenter(center1);

        parent2 = Parent.builder()
                .name("ParentB")
                .auth(Auth.PARENT)
                .build();
        parent3 = Parent.builder()
                .name("ParentC")
                .auth(Auth.PARENT)
                .build();
        teacher1 = Teacher.builder()
                .center(center1)
                .approval(Approval.WAITING)
                .name("TeacherA")
                .auth(Auth.TEACHER)
                .build();
        teacher2 = Teacher.builder()
                .center(center1)
                .approval(Approval.ACCEPT)
                .name("TeacherB")
                .auth(Auth.TEACHER)
                .build();

        board1 = Board.createBoard("홍보게시판", BoardKind.NORMAL, center1, false);
        board2 = Board.createBoard("자유게시판", BoardKind.NORMAL, center1, true);
        board3 = Board.createBoard("정보게시판", BoardKind.NORMAL, center1, true);
        board4 = Board.createBoard("장터게시판", BoardKind.NORMAL, center2, true);
        board5 = Board.createBoard("영상게시판", BoardKind.NORMAL, center3, true);

        post1 = Creator.createPost("제목1", "내용1", true, board1, parent1);
        post2 = Creator.createPost("제목2", "내용2", true, board1, parent1);
        post3 = Creator.createPost("제목3", "내용3", true, board1, parent1);
        post4 = Creator.createPost("제목4", "내용4", true, board1, parent1);
        post5 = Creator.createPost("제목5", "내용5", true, board2, parent1);
        post6 = Creator.createPost("제목6", "내용6", true, board2, parent1);
        post7 = Creator.createPost("제목7", "내용7", true, board2, parent1);
        post8 = Creator.createPost("제목8", "내용8", true, board3, parent1);
        post9 = Creator.createPost("제목9", "내용9", true, board3, parent1);
        post10 = Creator.createPost("제목10", "내용10", true, board3, parent1);
        post11 = Creator.createPost("제목11", "내용11", true, board4, parent2);
        post12 = Creator.createPost("제목12", "내용12", true, board5, parent3);
        post13 = Creator.createPost("제목13", "내용13", true, board5, parent3);
    }

    @Test
    public void 유저와_게시글_아이디로_좋아요_찾기() throws Exception {
        //given
        em.persist(center1);
        em.persist(board1);
        Long postId = em.persist(post1).getId();
        em.persist(post2);
        Long parentId = em.persist(parent1).getId();
        PostHeart postHeart1 = new PostHeart(parent1, post1);
        PostHeart postHeart2 = new PostHeart(parent1, post2);
        PostHeart expected = em.persist(postHeart1);
        em.persist(postHeart2);
        em.flush();
        em.clear();
        //when
        PostHeart result = postHeartRepository.findByPostAndUser(parentId, postId)
                .orElse(null);
        //then
        assertThat(expected.getId())
                .isEqualTo(result.getId());
    }
}