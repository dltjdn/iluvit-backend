package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.board.BoardListDto;
import FIS.iLUVit.dto.board.BoardRequest;
import FIS.iLUVit.dto.board.StoryPreviewDto;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.BoardService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    User user1;
    Center center1;

    private MockMvc mockMvc;

    @InjectMocks
    private BoardController boardController;
    @Mock
    private BoardService boardService;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders.standaloneSetup(boardController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        user1 = Parent.builder()
                .id(1L)
                .name("ads")
                .build();

        center1 = Center.builder()
                .id(2L)
                .name("ASdfsaf")
                .build();
    }
    ObjectMapper objectMapper = new ObjectMapper();

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 게시판_생성() throws Exception {
        //given

        BoardRequest request = new BoardRequest("자유게시판", BoardKind.NORMAL);

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
                                objectMapper.writeValueAsString(request)
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
        mockMvc.perform(delete("/board/{boardId}", 1L))
                .andExpect(content().string("1"));
    }

    @Test
    public void 모두의_이야기_내_게시판_목록_조회() throws Exception {
        //given
        Board board1 = Board.createBoard("자유게시판", BoardKind.NORMAL, null, true);
        Board board2 = Board.createBoard("장터게시판", BoardKind.NORMAL, null, true);
        BoardListDto boardListDto = new BoardListDto();
        List<BoardListDto.BoardBookmarkDto> boardList = getBookmarkDTOS(board1, board2, boardListDto);
        boardListDto.addBoardList(boardList);
        given(boardService.findAllWithBookmark(any()))
                .willReturn(boardListDto);
        //when

        //then
        mockMvc.perform(get("/board/public")
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
        BoardListDto boardListDto = new BoardListDto();
        List<BoardListDto.BoardBookmarkDto> boardList = getBookmarkDTOS(board1, board2, boardListDto);
        boardListDto.addBoardList(boardList);
        given(boardService.findAllWithBookmarkInCenter(any(), any()))
                .willReturn(boardListDto);
        //when

        //then
        mockMvc.perform(get("/board/in-center/{centerId}", "1")
                        .header("Authorization", "Bearer (accessToken)"))
                .andExpect(jsonPath("$.bookmarkList[0].board_name").value("공지게시판"))
                .andExpect(jsonPath("$.boardList[0].board_name").value("정보게시판"));
    }

    @NotNull
    public List<BoardListDto.BoardBookmarkDto> getBookmarkDTOS(Board board1, Board board2, BoardListDto boardListDTO) {
        BoardListDto.BoardBookmarkDto boardBookmarkDTO1 = new BoardListDto.BoardBookmarkDto(board1);
        BoardListDto.BoardBookmarkDto boardBookmarkDTO2 = new BoardListDto.BoardBookmarkDto(board2);
        List<BoardListDto.BoardBookmarkDto> bookmarkList = Arrays.asList(boardBookmarkDTO1);
        List<BoardListDto.BoardBookmarkDto> boardList = Arrays.asList(boardBookmarkDTO2);
        boardListDTO.addBookmarkList(bookmarkList);
        return boardList;
    }

    @Test
    public void 이야기_홈에서_센터_게시판_띄워주기_비회원() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = new ArrayList<>();
        storyPreviewDtoList.add(new StoryPreviewDto(null));


        String url = "/board/home";

        Mockito.doReturn(storyPreviewDtoList)
                .when(boardService)
                .findCenterStory(null);
        //when

        ResultActions resultActions = mockMvc.perform(
                get(url)
        );
        //then

        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        storyPreviewDtoList
                )));
    }

    @Test
    public void 이야기_홈에서_센터_게시판_띄워주기_유저X() throws Exception {
        //given

        String url = "/board/home";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;
        Mockito.doThrow(new UserException(error))
                .when(boardService)
                .findCenterStory(any(Long.class));
        //when

        ResultActions resultActions = mockMvc.perform(get(url)
                .header("Authorization", createJwtToken(user1)));

        //then

        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 이야기_홈에서_센터_게시판_띄워주기_성공() throws Exception {
        //given
        List<StoryPreviewDto> storyPreviewDtoList = new ArrayList<>();
        storyPreviewDtoList.add(new StoryPreviewDto(null));
        storyPreviewDtoList.add(new StoryPreviewDto(center1));

        String url = "/board/home";
        Mockito.doReturn(storyPreviewDtoList)
                .when(boardService)
                .findCenterStory(any(Long.class));
        //when

        ResultActions resultActions = mockMvc.perform(get(url)
                .header("Authorization", createJwtToken(user1)));

        //then

        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        storyPreviewDtoList
                )));

    }



}