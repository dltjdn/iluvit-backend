package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.repository.BoardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        CreateBoardRequest request1 = new CreateBoardRequest("에프아이솔루션", BoardKind.NORMAL);

        Board board1 = Board.createBoard(request1.getBoard_name(), request1.getBoardKind(), null, false);

        Long fakeBoardId = 1L;
        ReflectionTestUtils.setField(board1, "id", fakeBoardId);
        //mocking
        given(boardRepository.save(any()))
                .willReturn(board1);
        given(boardRepository.findById(fakeBoardId))
                .willReturn(Optional.of(board1));

        //when
        Long newBoardId = boardService.create(null, request1);

        //then
        Board findBoard = boardRepository.findById(newBoardId).get();
        Assertions.assertThat(board1).isEqualTo(findBoard);

    }

}