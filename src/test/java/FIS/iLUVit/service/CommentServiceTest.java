package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

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
                .id(1L)
                .name("나")
                .auth(Auth.PARENT)
                .build();
        board1 = CreateTest.createBoard(2L, "자유게시판", BoardKind.NORMAL, null, true);
        post1 = Creator.createPost(3L,"제목", "내용", true, board1, user1);
        comment1 = Creator.createComment(4L,true, "안녕", post1, user1);
        comment2 = Creator.createComment(5L,true, "하세", post1, user1);
        comment3 = Creator.createComment(6L,true, "요", post1, user1);
        comment4 = Creator.createComment(7L,true, "ㅋㅋ", post1, user1);
    }

    @Test
    public void 댓글_등록_비회원() throws Exception {
        //given

        //when

        //then
    }

}