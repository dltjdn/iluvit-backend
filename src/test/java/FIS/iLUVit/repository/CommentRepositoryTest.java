package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.BoardKind;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.CommentRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
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
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    Board board1;
    Post post1;
    User user1;
    Comment comment1;
    Comment comment2;
    Comment comment3;
    Comment comment4;

    @BeforeEach
    public void init() {
        user1 = Parent.builder()
                .name("나")
                .auth(Auth.PARENT)
                .build();
        board1 = CreateTest.createBoard("자유게시판", BoardKind.NORMAL, null, true);
        post1 = Creator.createPost("제목", "내용", true, board1, user1);
        comment1 = Creator.createComment(true, "안녕", post1, user1);
        comment2 = Creator.createComment(true, "하세", post1, user1);
        comment3 = Creator.createComment(true, "요", post1, user1);
        comment4 = Creator.createComment(true, "ㅋㅋ", post1, user1);
    }

    @Test
    public void 유저로_댓글_찾기() throws Exception {
        //given
        em.persist(board1);
        em.persist(post1);
        em.persist(user1);
        em.persist(comment1);
        em.persist(comment2);
        em.persist(comment3);
        em.persist(comment4);
        //when
        Slice<Comment> commentSlice = commentRepository
                .findByUser(user1.getId(), PageRequest.of(0, 10));
        //then
        List<Comment> content = commentSlice.getContent();
        assertThat(content).extracting("content")
                .contains("안녕", "하세", "요", "ㅋㅋ");
    }

    @Test
    public void 게시글_유저_익명정보로_댓글1개_찾기() throws Exception {
        //given
        em.persist(board1);
        em.persist(post1);
        em.persist(user1);
        em.persist(comment1);
        em.persist(comment2);
        em.persist(comment3);
        em.persist(comment4);
        //when
        Comment findComment = commentRepository
                .findFirstByPostAndUserAndAnonymous(post1, user1, true)
                .orElse(null);

        //then

        assertThat(findComment).isNotNull();

    }
}