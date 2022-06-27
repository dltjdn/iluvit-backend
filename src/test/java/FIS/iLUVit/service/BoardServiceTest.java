package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Test
    public void 모두의_이야기_게시판_추가() throws Exception {
        //given
        CreateBoardRequest request = new CreateBoardRequest("에프아이솔루션", BoardKind.NORMAL);
        Board newBoard = Board.createBoard(request.getBoard_name(), request.getBoardKind(), null, false);

        Long fakeBoardId = 1234L;
        ReflectionTestUtils.setField(newBoard, "id", fakeBoardId);

        //mocking


        //when

        //then
    }

}