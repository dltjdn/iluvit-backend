package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.BookmarkService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookmarkControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BookmarkController bookmarkController;

    @Mock
    private BookmarkService bookmarkService;

    ObjectMapper objectMapper;

    User user;

    Board board1;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookmarkController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper();

        user = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .build();

        board1 = CreateTest.createBoard(2L, "자유게시판", BoardKind.NORMAL, null, true);
    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("symmetricKey"));
    }

    @Test
    public void 메인화면_목록조회_비회원() throws Exception {
        //given
        BookmarkMainDTO dto = new BookmarkMainDTO();
        final String url = "/bookmark-main";
        Mockito.doReturn(dto)
                .when(bookmarkService)
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
        BookmarkMainDTO dto = new BookmarkMainDTO();
        final String url = "/bookmark-main";
        Mockito.doReturn(dto)
                .when(bookmarkService)
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
        final String url = "/bookmark/{board_id}";
        Mockito.doThrow(new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS))
                .when(bookmarkService)
                .create(any(), any());
        //when
        //then
        Assertions.assertThatThrownBy(() -> mockMvc.perform(
                MockMvcRequestBuilders.post(url, 2)
        )).hasCause(new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS));

    }


    @Test
    public void 북마크_추가_회원() throws Exception {
        //given
        final String url = "/bookmark/{board_id}";
        Mockito.doReturn(2L)
                .when(bookmarkService)
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
    public void 북마크_삭제_회원() throws Exception {
        //given
        final String url = "/bookmark/{bookmark_id}";
        Mockito.doReturn(2L)
                .when(bookmarkService)
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