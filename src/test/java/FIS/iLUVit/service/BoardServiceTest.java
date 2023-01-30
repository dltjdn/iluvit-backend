package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BoardListDto;
import FIS.iLUVit.controller.dto.BoardRequest;
import FIS.iLUVit.controller.dto.StoryPreviewDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.service.createmethod.CreateTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    // mock 객체를 만들어 반환
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CenterRepository centerRepository;

    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChildRepository childRepository;

    // @mock 객체(BoardRepository) 를 BoardService 에 주입
    @InjectMocks
    private BoardService boardService;

    ObjectMapper objectMapper;

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
    Child child2;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        parent1 = Parent.builder()
                .id(0L)
                .auth(Auth.PARENT)
                .build();

        center1 = createCenter(1L, "떡잎유치원");
        center2 = createCenter(2L, "팡팡어린이집");

        teacher1 = Teacher.builder()
                .id(50L)
                .auth(Auth.TEACHER)
                .center(center1)
                .approval(Approval.ACCEPT)
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
                .center(center1)
                .approval(Approval.ACCEPT)
                .build();

        child2 = Child.builder()
                .name("어린이2")
                .center(center2)
                .approval(Approval.ACCEPT)
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

    }

    @Test
    public void 게시판_추가_비회원_접근_제한() throws Exception {
        //given
        BoardRequest request = new BoardRequest("자유게시판", BoardKind.NORMAL);

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.create(null, null, request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 모두의_이야기_게시판_추가_이름_중복() throws Exception {
        //given
        BoardRequest request = new BoardRequest("자유게시판", BoardKind.NORMAL);

        Mockito.doReturn(Optional.ofNullable(board1))
                .when(boardRepository)
                .findByName(request.getBoard_name());
        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.create(parent1.getId(), null, request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.BOARD_NAME_DUPLICATION);

    }

    @Test
    public void 모두의_이야기_게시판_추가_성공() throws Exception {
        //given
        BoardRequest request = new BoardRequest("자유게시판", BoardKind.NORMAL);
        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), null, false);
        Long fakeId = 1L;
        ReflectionTestUtils.setField(board, "id", fakeId);

        //mocking
        given(boardRepository.save(any()))
                .willReturn(board);
        given(boardRepository.findById(fakeId))
                .willReturn(Optional.of(board));

        //when
        Long newId = boardService.create(parent1.getId(), null, request);

        //then
        Board findBoard = boardRepository.findById(newId).get();
        assertThat(findBoard.getName()).isEqualTo("자유게시판");
        assertThat(findBoard.getBoardKind()).isEqualTo(BoardKind.NORMAL);
        assertThat(findBoard.getCenter()).isEqualTo(null);
        assertThat(findBoard.getIsDefault()).isEqualTo(false);

    }
    
    @Test
    public void 센터의_이야기_게시판_추가_센터의_학부모X() throws Exception {
        BoardRequest request = new BoardRequest("공지게시판", BoardKind.NORMAL);
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.ofNullable(center1))
                .when(centerRepository)
                .findById(1L);
        //when
        UserException result = assertThrows(UserException.class,
                () -> boardService.create(parent1.getId(), 1L, request));

        //then

    }

    @Test
    public void 센터의_이야기_게시판_추가_학부모의_아이_센터에_속하지않음() throws Exception {
        BoardRequest request = new BoardRequest("공지게시판", BoardKind.NORMAL);
        Mockito.doReturn(Optional.ofNullable(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.ofNullable(center1))
                .when(centerRepository)
                .findById(1L);

        Mockito.doReturn(List.of())
                .when(childRepository)
                .findByParentAndCenter(parent1.getId(), 1L);


        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.create(parent1.getId(), 1L, request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);

    }

    @Test
    public void 센터의_이야기_게시판_추가_교사_센터에_속하지않음() throws Exception {
        BoardRequest request = new BoardRequest("공지게시판", BoardKind.NORMAL);
        Mockito.doReturn(Optional.ofNullable(center2))
                .when(centerRepository)
                .findById(2L);

        Mockito.doReturn(Optional.ofNullable(teacher1))
                .when(userRepository)
                .findById(1000L);

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.create(1000L, 2L, request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);

    }

    @Test
    public void 센터의_이야기_게시판_추가_이름_중복() throws Exception {
        //given
        BoardRequest request = new BoardRequest("공지게시판", BoardKind.NORMAL);

        Mockito.doReturn(Optional.ofNullable(board3))
                .when(boardRepository)
                .findByNameWithCenter(request.getBoard_name(), 1L);

        Mockito.doReturn(Optional.ofNullable(center1))
                .when(centerRepository)
                .findById(1L);

        Mockito.doReturn(Optional.ofNullable(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(List.of(child1))
                .when(childRepository)
                .findByParentAndCenter(0L, 1L);
        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.create(parent1.getId(), 1L, request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.BOARD_NAME_DUPLICATION);
    }

    @Test
    public void 센터의_이야기_게시판_추가_시설_아이디_오류() throws Exception {
        //given
        BoardRequest request = new BoardRequest("공지게시판", BoardKind.NORMAL);

        Mockito.doReturn(Optional.empty())
                .when(centerRepository)
                .findById(3L);
        //when
        CenterException result = assertThrows(CenterException.class,
                () -> boardService.create(parent1.getId(), 3L, request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CenterErrorResult.CENTER_NOT_EXIST);
    }

    @Test
    public void 센터의_이야기_게시판_추가_성공() throws Exception {
        //given
        BoardRequest request = new BoardRequest("자유게시판", BoardKind.NORMAL);
        Center center = new Center();
        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), center, false);
        Long fakeId = 1L;
        ReflectionTestUtils.setField(board, "id", fakeId);

        //mocking
        given(boardRepository.save(any()))
                .willReturn(board);
        given(boardRepository.findById(fakeId))
                .willReturn(Optional.of(board));

        //when
        Long newId = boardService.create(parent1.getId(), null, request);

        //then
        Board findBoard = boardRepository.findById(newId).get();
        assertThat(findBoard.getName()).isEqualTo("자유게시판");
        assertThat(findBoard.getBoardKind()).isEqualTo(BoardKind.NORMAL);
        assertThat(findBoard.getCenter()).isEqualTo(center);
        assertThat(findBoard.getIsDefault()).isEqualTo(false);

    }

    @Test
    public void 모두의_이야기_모든_게시판_조회() throws Exception {
        //given
        Mockito.doReturn(Arrays.asList(bookmark3))
                .when(boardBookmarkRepository)
                .findBoardByUser(parent1.getId());

        Mockito.doReturn(Arrays.asList(board1, board2))
                .when(boardRepository)
                .findByCenterIsNull();
        //when
        BoardListDto dto = boardService.findAllWithBookmark(parent1.getId());
        //then
        BoardListDto.BookmarkDTO board = dto.getBoardList().get(0);
        List<BoardListDto.BookmarkDTO> bookmarkList = dto.getBookmarkList();
        assertThat(board.getBoard_name()).isEqualTo(board1.getName());
        assertThat(board.getBookmark_id()).isNull();
        assertThat(bookmarkList).extracting("board_name")
                .contains(
                        "맛집게시판"
                );
        assertThat(bookmarkList).extracting("board_name")
                .contains(
                        bookmark3.getBoard().getName()
                );
    }

    @Test
    public void 센터의_이야기_모든_게시판_조회() throws Exception {
        //given
        Mockito.doReturn(Optional.of(center1))
                .when(centerRepository)
                .findById(1L);
        Mockito.doReturn(Arrays.asList(bookmark1, bookmark2))
                .when(boardBookmarkRepository)
                .findBoardByUserAndCenter(parent1.getId(), 1L);

        Mockito.doReturn(Arrays.asList(board3, board5, board6))
                .when(boardRepository)
                .findByCenter(1L);
        //when
        BoardListDto dto = boardService.findAllWithBookmarkInCenter(parent1.getId(), 1L);
        //then
        BoardListDto.BookmarkDTO board = dto.getBoardList().get(0);
        List<BoardListDto.BookmarkDTO> bookmarkList = dto.getBookmarkList();
        assertThat(board.getBoard_name()).isEqualTo(board6.getName());
        assertThat(board.getBookmark_id()).isNull();
        assertThat(bookmarkList).extracting("board_name")
                .contains(
                        "공지게시판",
                        "맛집게시판"
                );
    }

    @Test
    public void 게시판_삭제_비회원X() throws Exception {
        //given

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(null, 0L));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 게시판_삭제_게시판_아이디_오류() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(null))
                .when(boardRepository)
                .findById(0L);

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(parent1.getId(), 0L));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.BOARD_NOT_EXIST);
    }

    @Test
    public void 게시판_삭제_학부모() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board3))
                .when(boardRepository)
                .findById(any());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(0L, board3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
    }

    @Test
    public void 게시판_삭제_교사() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board3))
                .when(boardRepository)
                .findById(any());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(0L, board3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
    }


    @Test
    public void 게시판_삭제_원장_센터_NULL() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board3))
                .when(boardRepository)
                .findById(any());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(0L, board3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
    }

    @Test
    public void 게시판_삭제_원장_센터X() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board3))
                .when(boardRepository)
                .findById(any());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(0L, board3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
    }

    @Test
    public void 게시판_삭제_원장_센터O_기본_게시판() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board4))
                .when(boardRepository)
                .findById(any());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> boardService.remove(100L, board4.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
    }

    @Test
    public void 게시판_삭제_원장_센터O_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.ofNullable(board7))
                .when(boardRepository)
                .findById(12L);

        Mockito.doReturn(Optional.ofNullable(director2))
                .when(userRepository)
                .findById(0L);
        //when
        Long removedId = boardService.remove(0L, board7.getId());
        //then
        assertThat(removedId).isEqualTo(12L);
    }

    @Test
    public void 이야기_홈에서_센터의_게시판_띄워주기_비회원() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = new ArrayList<>();
        storyPreviewDtoList.addAll(List.of(new StoryPreviewDto(null)));
        //when
        List<StoryPreviewDto> stories = boardService.findCenterStory(null);
        //then

        assertThat(objectMapper.writeValueAsString(stories))
                .isEqualTo(objectMapper.writeValueAsString(storyPreviewDtoList));

    }

    @Test
    public void 이야기_홈에서_센터의_게시판_띄워주기_유저X() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = Arrays.asList(
                new StoryPreviewDto(null),
                new StoryPreviewDto(center1),
                new StoryPreviewDto(center2));

        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> boardService.findCenterStory(parent1.getId()));


        //then

        System.out.println("storyHome = " + objectMapper.writeValueAsString(storyPreviewDtoList));

        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 이야기_홈에서_센터의_게시판_띄워주기_학부모() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = Arrays.asList(
                new StoryPreviewDto(null),
                new StoryPreviewDto(center1),
                new StoryPreviewDto(center2));

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Arrays.asList(child1, child2))
                .when(userRepository)
                .findChildrenWithCenter(parent1.getId());
        //when
        List<StoryPreviewDto> result = boardService.findCenterStory(parent1.getId());

        //then

        System.out.println("storyHome = " + objectMapper.writeValueAsString(storyPreviewDtoList));
        System.out.println("result = " + objectMapper.writeValueAsString(result));
        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(storyPreviewDtoList));
    }

    @Test
    public void 이야기_홈에서_센터의_게시판_띄워주기_교사() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = Arrays.asList(
                new StoryPreviewDto(null),
                new StoryPreviewDto(center1));

        Mockito.doReturn(Optional.of(teacher1))
                .when(userRepository)
                .findById(teacher1.getId());
        //when
        List<StoryPreviewDto> result = boardService.findCenterStory(teacher1.getId());

        //then

        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(storyPreviewDtoList));
    }


}