package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.repository.BoardRepository;
import FIS.iLUVit.repository.BoardBookmarkRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.service.createmethod.CreateTest.*;
import static FIS.iLUVit.service.createmethod.CreateTest.createBookmark;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardBookmarkServiceTest {

    @Mock
    BoardBookmarkRepository boardBookmarkRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BoardRepository boardRepository;

    @InjectMocks
    BoardBookmarkService boardBookmarkService;

    Board board1;
    Board board2;
    Board board3;
    Board board4;
    Board board5;
    Board board6;
    Board board7;

    Center center1;
    Center center2;

    Bookmark bookmark1;
    Bookmark bookmark2;
    Bookmark bookmark3;

    Parent parent1;
    Teacher teacher1;
    Teacher director1;
    Teacher director2;

    Child child1;

    Post post1;
    Post post2;
    Post post3;
    Post post4;
    Post post5;
    Post post6;

    @BeforeEach
    public void init() {
        parent1 = Parent.builder()
                .id(0L)
                .auth(Auth.PARENT)
                .build();

        center1 = createCenter(1L, "떡잎유치원");
        center2 = createCenter(2L, "팡팡어린이집");

        teacher1 = Teacher.builder()
                .auth(Auth.TEACHER)
                .center(center1)
                .build();

        director1 = Teacher.builder()
                .auth(Auth.DIRECTOR)
                .center(null)
                .build();

        director2 = Teacher.builder()
                .auth(Auth.DIRECTOR)
                .center(center2)
                .build();

        child1 = Child.builder()
                .name("어린이")
                .build();

        board1 = createBoard(3L, "자유게시판", BoardKind.NORMAL, null, true);
        board2 = createBoard(4L, "맛집게시판", BoardKind.NORMAL, null, true);
        board3 = createBoard(5L, "공지게시판", BoardKind.NORMAL, center1, true);
        board4 = createBoard(6L, "공지게시판", BoardKind.NORMAL, center2, true);
        board5 = createBoard(9L, "맛집게시판", BoardKind.FOOD, center1, true);
        board6 = createBoard(10L, "장터게시판", BoardKind.MARKET, center1, true);
        board7 = createBoard(12L, "모임게시판", BoardKind.NORMAL, center2, false);


        bookmark1 = createBookmark(7L, board3, parent1);
        bookmark2 = createBookmark(8L, board5, parent1);
        bookmark3 = createBookmark(11L, board2, parent1);

        post1 = Creator.createPost(13L, "제목1", "내용1", true, board1, parent1);
        post2 = Creator.createPost(14L, "제목2", "내용2", true, board2, parent1);
        post3 = Creator.createPost(15L, "제목3", "내용3", true, board3, parent1);
        post4 = Creator.createPost(16L, "제목4", "내용4", true, board4, parent1);
        post5 = Creator.createPost(17L, "제목5", "내용5", true, board1, parent1);
        post6 = Creator.createPost(18L, "제목6", "내용6", true, board2, parent1);
    }

    @Test
    public void 북마크_목록_한번에() throws Exception {
        //given
        Mockito.doReturn(Arrays.asList(bookmark1, bookmark2, bookmark3))
                .when(boardBookmarkRepository)
                .findByUserWithBoardAndCenter(parent1.getId());

        Mockito.doReturn(Arrays.asList(post3, post4, post5, post6))
                .when(boardBookmarkRepository)
                .findPostByBoard(parent1.getId());
        //when
        BookmarkMainDTO dto = boardBookmarkService.search(parent1.getId());
        //then
        List<BookmarkMainDTO.StoryDTO> stories = dto.getStories();

        assertThat(stories).extracting("story_name")
                .contains("모두의 게시판", "떡잎유치원", "팡팡어린이집");
    }

    @Test
    public void 북마크_목록_한번에_비회원() throws Exception {
        //given
        Mockito.doReturn(Arrays.asList(post5, post6))
                .when(boardRepository)
                .findPostByDefault();
        //when
        BookmarkMainDTO dto = boardBookmarkService.searchByDefault();
        //then
        List<BookmarkMainDTO.StoryDTO> stories = dto.getStories();
        List<BookmarkMainDTO.BoardDTO> boardDTOList = stories.get(0).getBoardDTOList();
        assertThat(stories).extracting("story_name")
                .containsExactly("모두의 이야기");
        assertThat(boardDTOList).extracting("board_name")
                .containsOnly("자유게시판", "맛집게시판");
    }


    @Test
    public void 북마크_생성_비회원() throws Exception {
        //given
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.create(null, board1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 북마크_생성_유저X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(any());
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.create(parent1.getId(), board1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 북마크_생성_게시판X() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(parent1))
                .when(userRepository)
                .findById(any());

        Mockito.doReturn(Optional.empty())
                .when(boardRepository)
                .findById(any());
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.create(parent1.getId(), board1.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.BOARD_NOT_EXIST);
    }

    @Test
    public void 북마크_생성_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(parent1))
                .when(userRepository)
                .findById(any());

        Mockito.doReturn(Optional.ofNullable(board1))
                .when(boardRepository)
                .findById(any());

        Mockito.doReturn(bookmark1)
                .when(boardBookmarkRepository)
                .save(any());
        //when
        Long bookmarkId = boardBookmarkService.create(parent1.getId(), board1.getId());
        //then
        assertThat(bookmarkId).isEqualTo(bookmark1.getId());
    }

    @Test
    public void 북마크_삭제_북마크X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(boardBookmarkRepository)
                .findById(any());
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.delete(parent1.getId(), bookmark1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.BOOKMARK_NOT_EXIST);
    }

    @Test
    public void 북마크_삭제_비회원() throws Exception {
        //given
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.delete(null, bookmark1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 북마크_삭제_권한없는_유저() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(bookmark1))
                .when(boardBookmarkRepository)
                .findById(bookmark1.getId());
        //when
        BookmarkException result = assertThrows(BookmarkException.class,
                () -> boardBookmarkService.delete(teacher1.getId(), bookmark1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 북마크_삭제_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(bookmark1))
                .when(boardBookmarkRepository)
                .findById(any());

        //when
        Long deletedId = boardBookmarkService.delete(parent1.getId(), bookmark1.getId());

        //then
        assertThat(deletedId).isEqualTo(bookmark1.getId());
    }

}