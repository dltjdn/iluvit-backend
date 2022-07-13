package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.BoardListDTO;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.service.BoardService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
    }

    private MockMvc mockMvc;
    @InjectMocks
    private BoardController boardController;
    @Mock
    private BoardService boardService;

    @Test
    public void 게시판_생성() throws Exception {
        //given

        given(boardService.create(eq(null), eq(1L), any()))
                .willReturn(2L);

        given(boardService.create(eq(null), eq(null), any()))
                .willReturn(3L);
        //when

        //then
        MvcResult resultOnCenter = mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("center_id", "1")
                        .content(
                                "{ \"board_name\": \"자유게시판\", \"boardKind\": \"NORMAL\" }"
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody1 = resultOnCenter.getResponse().getContentAsString();
        assertThat(responseBody1).isEqualTo("2");

        MvcResult resultOnNull = mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("center_id", (String) null)
                        .content(
                                "{ \"board_name\": \"자유게시판\", \"boardKind\": \"NORMAL\" }"
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody2 = resultOnNull.getResponse().getContentAsString();
        assertThat(responseBody2).isEqualTo("3");

    }

    @Test
    public void 게시판_삭제() throws Exception {
        //given
        given(boardService.remove(any(), any()))
                .willReturn(1L);
        //when

        //then
        mockMvc.perform(delete("/board/{board_id}", 1L))
                .andExpect(content().string("1"));
    }

    @Test
    public void 모두의_이야기_내_게시판_목록_조회() throws Exception {
        //given
        Board board1 = Board.createBoard("자유게시판", BoardKind.NORMAL, null, true);
        Board board2 = Board.createBoard("장터게시판", BoardKind.NORMAL, null, true);
        BoardListDTO boardListDTO = new BoardListDTO();
        List<BoardListDTO.BookmarkDTO> boardList = getBookmarkDTOS(board1, board2, boardListDTO);
        boardListDTO.setBoardList(boardList);
        given(boardService.findAllWithBookmark(any()))
                .willReturn(boardListDTO);
        //when

        //then
        mockMvc.perform(get("/board/modu")
                        .header("Authorization", "Bearer (accessToken)"))
                .andExpect(jsonPath("$.bookmarkList[0].board_name").value("자유게시판"))
                .andExpect(jsonPath("$.boardList[0].board_name").value("장터게시판"));

    }

    @Test
    public void 시설_유치원_내_게시판_목록_조회() throws Exception {
        //given
        Center center = new Center();
        ReflectionTestUtils.setField(center, "id", 1L);
        Board board1 = Board.createBoard("공지게시판", BoardKind.NORMAL, center, true);
        Board board2 = Board.createBoard("정보게시판", BoardKind.NORMAL, center, true);
        BoardListDTO boardListDTO = new BoardListDTO();
        List<BoardListDTO.BookmarkDTO> boardList = getBookmarkDTOS(board1, board2, boardListDTO);
        boardListDTO.setBoardList(boardList);
        given(boardService.findAllWithBookmarkInCenter(any(), any()))
                .willReturn(boardListDTO);
        //when

        //then
        mockMvc.perform(get("/board/inCenter/{center_id}", "1")
                        .header("Authorization", "Bearer (accessToken)"))
                .andExpect(jsonPath("$.bookmarkList[0].board_name").value("공지게시판"))
                .andExpect(jsonPath("$.boardList[0].board_name").value("정보게시판"));
    }

    @NotNull
    public List<BoardListDTO.BookmarkDTO> getBookmarkDTOS(Board board1, Board board2, BoardListDTO boardListDTO) {
        BoardListDTO.BookmarkDTO bookmarkDTO1 = new BoardListDTO.BookmarkDTO(board1);
        BoardListDTO.BookmarkDTO bookmarkDTO2 = new BoardListDTO.BookmarkDTO(board2);
        List<BoardListDTO.BookmarkDTO> bookmarkList = Arrays.asList(bookmarkDTO1);
        List<BoardListDTO.BookmarkDTO> boardList = Arrays.asList(bookmarkDTO2);
        boardListDTO.setBookmarkList(bookmarkList);
        return boardList;
    }

}