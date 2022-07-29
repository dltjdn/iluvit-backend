package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.CommentErrorResult;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.repository.CommentHeartRepository;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CommentHeartServiceTest {
    @InjectMocks
    CommentHeartService commentHeartService;

    @Mock
    CommentHeartRepository commentHeartRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;

    Board board1;
    Post post1;
    User user1;
    User user2;
    Comment comment1;
    Comment comment2;
    Comment comment3;
    Comment comment4;
    CommentHeart commentHeart1;
    CommentHeart commentHeart2;

    @BeforeEach
    public void init() {
        user1 = Parent.builder()
                .id(1L)
                .name("나")
                .auth(Auth.PARENT)
                .build();
        user2 = Parent.builder()
                .id(11L)
                .name("userA")
                .auth(Auth.PARENT)
                .build();
        board1 = CreateTest.createBoard(2L, "자유게시판", BoardKind.NORMAL, null, true);
        post1 = Creator.createPost(3L,"제목", "내용", true, board1, user1);
        comment1 = Creator.createComment(4L,true, "안녕", post1, user1);
        comment2 = Creator.createComment(5L,true, "하세", post1, user1);
        comment3 = Creator.createComment(6L,true, "요", post1, user1);
        comment4 = Creator.createComment(7L,true, "ㅋㅋ", post1, user1);
        commentHeart1 = Creator.createCommentHeart(8L, user1, comment1);
        commentHeart2 = Creator.createCommentHeart(9L, user2, comment1);
    }

    @Test
    public void 좋아요_등록_비회원() throws Exception {
        //given

        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.save(null, comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
    }

    @Test
    public void 좋아요_등록_댓글X() throws Exception {
        //given

        Mockito.doReturn(Optional.empty())
                .when(commentRepository)
                .findById(comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.save(user1.getId(), comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.NO_EXIST_COMMENT);
    }

    @Test
    public void 좋아요_등록_이미_존재() throws Exception {
        //given
        Mockito.doReturn(Optional.of(commentHeart1))
                .when(commentHeartRepository)
                .findByUserAndComment(user1.getId(), comment1.getId());

        Mockito.doReturn(Optional.of(comment1))
                .when(commentRepository)
                .findById(comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.save(user1.getId(), comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.ALREADY_EXIST_HEART);
    }

    @Test
    public void 좋아요_등록_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(commentHeartRepository)
                .findByUserAndComment(user1.getId(), comment1.getId());

        Mockito.doReturn(user1)
                .when(userRepository)
                .getById(any());

        Mockito.doReturn(Optional.of(comment1))
                .when(commentRepository)
                .findById(comment1.getId());

        Mockito.doReturn(commentHeart1)
                .when(commentHeartRepository)
                .save(any());
        //when
        Long savedId = commentHeartService.save(user1.getId(), comment1.getId());

        //then
        assertThat(savedId).isEqualTo(commentHeart1.getId());

    }

    @Test
    public void 좋아요_삭제_비회원() throws Exception {
        //given

        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.delete(null, comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
    }

    @Test
    public void 좋아요_삭제_좋아요X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(commentHeartRepository)
                .findByUserAndComment(user1.getId(), comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.delete(user1.getId(), comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.NO_EXIST_COMMENT_HEART);
    }

    @Test
    public void 좋아요_삭제_유저와_좋아요_작성자가_일치하지_않은_경우() throws Exception {
        //given
        Mockito.doReturn(Optional.of(commentHeart2))
                .when(commentHeartRepository)
                .findByUserAndComment(user1.getId(), comment1.getId());
        //when
        CommentException result = assertThrows(CommentException.class,
                () -> commentHeartService.delete(user1.getId(), comment1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
    }

    @Test
    public void 좋아요_삭제_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(commentHeart1))
                .when(commentHeartRepository)
                .findByUserAndComment(user1.getId(), comment1.getId());
        Mockito.doNothing()
                .when(commentHeartRepository)
                .delete(any());
        //when
        Long deletedId = commentHeartService.delete(user1.getId(), comment1.getId());

        //then
        assertThat(deletedId)
                .isEqualTo(commentHeart1.getId());
    }
}