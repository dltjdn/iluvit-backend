package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.board.StoryDto;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Bookmark;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.BoardBookmarkService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BoardBookmarkControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoardBookmarkService boardBookmarkService;

    @InjectMocks
    private BoardBookmarkController bookmarkController;

    ObjectMapper objectMapper;

    User user;

    Board board1;

    Bookmark bookmark1;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookmarkController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper();

        user = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .build();

        board1 = CreateTest.createBoard(2L, "자유게시판", BoardKind.NORMAL, null, true);
        bookmark1 = CreateTest.createBookmark(3L, board1, user);
    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 메인화면_목록조회_비회원() throws Exception {
        //given
        List<StoryDto> dto = new ArrayList<>();

        final String url = "/board-bookmark/main";
        Mockito.doReturn(dto)
                .when(boardBookmarkService)
                .searchByDefault();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url));

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void 메인화면_목록조회_회원() throws Exception {
        //given
        List<StoryDto> dto = new ArrayList<>();
        final String url = "/board-bookmark/main";
        Mockito.doReturn(dto)
                .when(boardBookmarkService)
                .search(any());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", createJwtToken()));

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }


    @Test
    public void 북마크_추가_비회원() throws Exception {
        //given
        final String url = "/board-bookmark/{boardId}";
        BookmarkErrorResult error = BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .create(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url, 2));
        //then

        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 북마크_추가_회원X() throws Exception {
        //given
        final String url = "/board-bookmark/{boardId}";
        BookmarkErrorResult error = BookmarkErrorResult.USER_NOT_EXIST;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .create(user.getId(), board1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url, 2)
                .header("Authorization", createJwtToken()));
        //then

        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 북마크_추가_게시판X() throws Exception {
        //given
        final String url = "/board-bookmark/{boardId}";
        BookmarkErrorResult error = BookmarkErrorResult.BOARD_NOT_EXIST;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .create(user.getId(), 9999L);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, 9999)
                        .header("Authorization", createJwtToken()));
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 북마크_추가_회원() throws Exception {
        //given
        final String url = "/board-bookmark/{boardId}";
        Mockito.doReturn(2L)
                .when(boardBookmarkService)
                .create(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url, 2)
                        .header("Authorization", createJwtToken())
        );

        //then
        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(2L)));;

    }

    @Test
    public void 북마크_삭제_비회원() throws Exception {
        //given
        final String url = "/board-bookmark/{bookmarkId}";
        BookmarkErrorResult error = BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .delete(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, bookmark1.getId()));
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 북마크_삭제_북마크X() throws Exception {
        //given
        final String url = "/board-bookmark/{bookmarkId}";
        BookmarkErrorResult error = BookmarkErrorResult.BOOKMARK_NOT_EXIST;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .delete(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, bookmark1.getId())
                        .header("Authorization", createJwtToken()));
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 북마크_삭제_회원권한X() throws Exception {
        //given
        final String url = "/board-bookmark/{bookmarkId}";
        BookmarkErrorResult error = BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new BookmarkException(error))
                .when(boardBookmarkService)
                .delete(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, bookmark1.getId())
                        .header("Authorization", createJwtToken()));
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 북마크_삭제_회원() throws Exception {
        //given
        final String url = "/board-bookmark/{bookmarkId}";
        Mockito.doReturn(2L)
                .when(boardBookmarkService)
                .delete(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, 2)
                        .header("Authorization", createJwtToken())
        );

        //then
        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(2L)));

    }

}