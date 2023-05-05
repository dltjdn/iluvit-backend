package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.BoardKind;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.CommentHeartRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class CommentHeartRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentHeartRepository commentHeartRepository;

    Board board1;
    Post post1;
    User user1;
    User user2;

    Comment comment1;
    Comment comment2;
    Comment comment3;
    Comment comment4;

    CommentHeart commentHeart1;

    @BeforeEach
    public void init() {
        user1 = Parent.builder()
                .name("나")
                .auth(Auth.PARENT)
                .build();
        user2 = Parent.builder()
                .name("유저A")
                .auth(Auth.PARENT)
                .build();
        board1 = CreateTest.createBoard("자유게시판", BoardKind.NORMAL, null, true);
        post1 = Creator.createPost("제목", "내용", true, board1, user1);
        comment1 = Creator.createComment(true, "안녕", post1, user2);
        comment2 = Creator.createComment(true, "하세", post1, user1);
        comment3 = Creator.createComment(true, "요", post1, user1);
        comment4 = Creator.createComment(true, "ㅋㅋ", post1, user1);

        commentHeart1 = Creator.createCommentHeart(user1, comment1);
    }

    @Test
    public void 유저와_댓글로_좋아요_찾기() throws Exception {
        //given
        em.persist(board1);
        em.persist(post1);
        em.persist(user1);
        em.persist(user2);
        em.persist(comment1);
        em.persist(comment2);
        em.persist(comment3);
        em.persist(comment4);
        em.persist(commentHeart1);
        //when
        CommentHeart findCommentHeart = commentHeartRepository
                .findByUserAndComment(user1.getId(), comment1.getId())
                .orElse(null);
        //then
        assertThat(findCommentHeart).isNotNull();
    }

}