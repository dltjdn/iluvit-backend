package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.CommentDTO;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.PostAlarm;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
    RegisterCommentRequest request = new RegisterCommentRequest();

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
        request.setAnonymous(true);
        request.setContent("하이");
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentService.registerComment(null, post1.getId(), null, request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 댓글_등록_유저X() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("하이");

        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(user1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> commentService.registerComment(user1.getId(), post1.getId(), null, request));
        //then
        assertThat(result.getClass())
                .isEqualTo(UserException.class);
    }

    @Test
    public void 댓글_등록_게시글X() throws Exception {
        //given
        request.setAnonymous(true);
        request.setContent("하이");

        Mockito.doReturn(Optional.of(user1))
                .when(userRepository)
                .findById(user1.getId());

        Mockito.doReturn(Optional.empty())
                .when(postRepository)
                .findById(post1.getId());
        //when
        PostException result = assertThrows(PostException.class,
                () -> commentService.registerComment(user1.getId(), post1.getId(), null, request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.POST_NOT_EXIST);
    }

    @Test
    public void 댓글_등록_성공() throws Exception {
        //given
        MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class);

        request.setAnonymous(true);
        request.setContent("하이");

        Mockito.doReturn(Optional.of(user1))
                .when(userRepository)
                .findById(user1.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());

        alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                .thenReturn("회원 {0}로 부터 새로운 채팅을 받았어요");

        AlarmEvent alarmEvent = new AlarmEvent(new PostAlarm(user1, post1, comment2));
        alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                .thenReturn(alarmEvent);

        Mockito.doReturn(comment2)
                .when(commentRepository)
                .save(any(Comment.class));
        //when
        Long savedId = commentService.registerComment(user1.getId(), post1.getId(), null, request);
        //then
        assertThat(savedId).isEqualTo(comment2.getId());

        alarmUtils.close();
    }

    @Test
    public void 대댓글_등록_성공() throws Exception {
        //given
        MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class);

        request.setAnonymous(true);
        request.setContent("하이");

        Mockito.doReturn(Optional.of(user1))
                .when(userRepository)
                .findById(user1.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());

        alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                .thenReturn("회원 {0}로 부터 새로운 채팅을 받았어요");

        AlarmEvent alarmEvent = new AlarmEvent(new PostAlarm(user1, post1, comment2));
        alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                .thenReturn(alarmEvent);

        Mockito.doReturn(comment2)
                .when(commentRepository)
                .save(any(Comment.class));

        Mockito.doReturn(comment1)
                .when(commentRepository)
                .getById(any(Long.class));
        //when
        Long savedId = commentService.registerComment(user1.getId(), post1.getId(), comment1.getId(), request);
        //then
        assertThat(savedId).isEqualTo(comment2.getId());

        alarmUtils.close();
    }

    @Test
    public void 댓글_삭제_비회원() throws Exception {
        //given
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentService.deleteComment(null, comment1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 댓글_삭제_댓글X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(commentRepository)
                .findById(comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentService.deleteComment(user1.getId(), comment1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.NO_EXIST_COMMENT);
    }

    @Test
    public void 댓글_삭제_권한X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(comment1))
                .when(commentRepository)
                .findById(comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentService.deleteComment(999L, comment1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 댓글_삭제_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(comment1))
                .when(commentRepository)
                .findById(comment1.getId());
        //when

        Long deletedId = commentService.deleteComment(user1.getId(), comment1.getId());
        //then
        assertThat(deletedId)
                .isEqualTo(comment1.getId());
        assertThat(comment1.getContent())
                .isEqualTo("삭제된 댓글입니다.");
        assertThat(comment1.getUser())
                .isNull();
    }

    @Test
    public void 유저가_쓴_댓글_조회() throws Exception {
        //given
        List<Comment> comments = Arrays.asList(comment1, comment2, comment3, comment4);
        SliceImpl<Comment> commentSlice = new SliceImpl<>(comments);
        //when
        Mockito.doReturn(commentSlice)
                .when(commentRepository)
                .findByUser(user1.getId(), PageRequest.of(0, 10));

        Slice<CommentDTO> commentDTOS = commentService.searchByUser(user1.getId(), PageRequest.of(0, 10));
        //then

        assertThat(commentDTOS.getContent())
                .extracting("content")
                .containsOnly(
                        "안녕",
                        "하세",
                        "요",
                        "ㅋㅋ"
                );
    }
}