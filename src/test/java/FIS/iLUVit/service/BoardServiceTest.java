package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    // mock 객체를 만들어 반환
    @Mock
    private BoardRepository boardRepository;

    // @mock 객체(BoardRepository) 를 BoardService 에 주입
    @InjectMocks
    private BoardService boardService;

    @Test
    public void 모두의_이야기_게시판_추가() throws Exception {
        //given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", BoardKind.NORMAL);
        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), null, false);
        Long fakeId = 1L;
        ReflectionTestUtils.setField(board, "id", fakeId);

        //mocking
        given(boardRepository.save(any()))
                .willReturn(board);
        given(boardRepository.findById(fakeId))
                .willReturn(Optional.of(board));

        //when
        Long newId = boardService.create(null, request);

        //then
        Board findBoard = boardRepository.findById(newId).get();
        assertThat(findBoard.getName()).isEqualTo("자유게시판");
        assertThat(findBoard.getBoardKind()).isEqualTo(BoardKind.NORMAL);
        assertThat(findBoard.getCenter()).isEqualTo(null);
        assertThat(findBoard.getIsDefault()).isEqualTo(false);

    }

    @Test
    public void 모두의_이야기_게시판_추가_중복이름검증() throws Exception {
        //given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", BoardKind.NORMAL);
        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), null, false);

        //mocking
        given(boardRepository.findByName(request.getBoard_name()))
                .willReturn(Optional.of(board));

        //when
        //the
        assertThatThrownBy(() -> {
            boardService.create(null, request);
        }).isInstanceOf(BoardException.class)
                .hasMessageContaining("이름 중복");
    }

    @Test
    public void 센터의_이야기_게시판_추가() throws Exception {
        //given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", BoardKind.NORMAL);
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
        Long newId = boardService.create(null, request);

        //then
        Board findBoard = boardRepository.findById(newId).get();
        assertThat(findBoard.getName()).isEqualTo("자유게시판");
        assertThat(findBoard.getBoardKind()).isEqualTo(BoardKind.NORMAL);
        assertThat(findBoard.getCenter()).isEqualTo(center);
        assertThat(findBoard.getIsDefault()).isEqualTo(false);

    }

    @Test
    public void 센터의_이야기_게시판_추가_중복이름검증() throws Exception {
        //given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", BoardKind.NORMAL);
        Center center = new Center();
        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), center, false);

        //mocking
        given(boardRepository.findByName(request.getBoard_name()))
                .willReturn(Optional.of(board));

        //when
        //the
        assertThatThrownBy(() -> {
            boardService.create(null, request);
        }).isInstanceOf(BoardException.class)
                .hasMessageContaining("이름 중복");
    }

}