package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.controller.dto.CreateChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.ChatErrorResult;
import FIS.iLUVit.exception.ChatException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.BookmarkService;
import FIS.iLUVit.service.ChatService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    MockMvc mockMvc;

    @Mock
    ChatService chatService;

    @InjectMocks
    ChatController chatController;

    ObjectMapper objectMapper;

    User receiver;
    User sender;

    Board board1;

    Post post1;

    ChatRoom chatRoom1;
    ChatRoom chatRoom2;

    Chat chat1;
    Chat chat2;
    Chat chat3;
    Chat chat4;
    Chat chat5;
    Chat chat6;

    CreateChatRequest request = new CreateChatRequest();
    CreateChatRoomRequest roomRequest = new CreateChatRoomRequest();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        receiver = Parent.builder()
                .id(1L)
                .nickName("sa")
                .name("나")
                .auth(Auth.PARENT)
                .build();

        sender = Parent.builder()
                .id(2L)
                .nickName("adsf")
                .name("상대방")
                .auth(Auth.PARENT)
                .build();

        board1 = CreateTest.createBoard(3L, "자유게시판", BoardKind.NORMAL, null, true);

        post1 = Creator.createPost(4L, "제목", "내용", true, board1, receiver);

        chatRoom1 = Creator.createChatRoom(5L, receiver, sender, post1);
        chatRoom2 = Creator.createChatRoom(6L, sender, receiver, post1);

        chat1 = Creator.createChat(7L,"안녕하세요", chatRoom1, sender, receiver);
        chat2 = Creator.createChat(8L,"안녕하세요", chatRoom2, sender, receiver);
        chat3 = Creator.createChat(9L,"감사해요", chatRoom1, receiver, sender);
        chat4 = Creator.createChat(10L, "감사해요", chatRoom2, receiver, sender);
        chat5 = Creator.createChat(11L, "잘있어요", chatRoom1, sender, receiver);
        chat6 = Creator.createChat(12L, "잘있어요", chatRoom2, sender, receiver);

    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", receiver.getId())
                .sign(Algorithm.HMAC512("symmetricKey"));
    }

    @Test
    public void 쪽지_작성_대화방_생성_비회원_접근() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 쪽지_작성_대화방_생성_자신에게_보냄() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        request.setReceiver_id(receiver.getId());

        final String url = "/chat";
        final ChatErrorResult error = ChatErrorResult.NO_SEND_TO_SELF;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(any(Long.class), any(CreateChatRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_유저X() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        request.setReceiver_id(receiver.getId());
        final String url = "/chat";
        final ChatErrorResult error = ChatErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(any(Long.class), any(CreateChatRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }
}