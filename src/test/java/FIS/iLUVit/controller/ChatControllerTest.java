package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ChatDto;
import FIS.iLUVit.controller.dto.ChatListDto;
import FIS.iLUVit.controller.dto.ChatRequest;
import FIS.iLUVit.controller.dto.ChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.ChatErrorResult;
import FIS.iLUVit.exception.ChatException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    ChatRequest request = new ChatRequest();
    ChatRoomRequest roomRequest = new ChatRoomRequest();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
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
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 쪽지_작성_대화방_생성_비회원_접근() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        final String url = "/user/chat";
        final ChatErrorResult error = ChatErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(eq(null), any(ChatRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_자신에게_보냄() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());

        final String url = "/user/chat";
        final ChatErrorResult error = ChatErrorResult.NO_SEND_TO_SELF;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(any(Long.class), any(ChatRequest.class));

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
        final String url = "/user/chat";
        final ChatErrorResult error = ChatErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(any(Long.class), any(ChatRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_게시글X() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        final String url = "/user/chat";
        final ChatErrorResult error = ChatErrorResult.POST_NOT_EXIST;

        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChat(any(Long.class), any(ChatRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_성공() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        final String url = "/user/chat";

        Mockito.doReturn(chat2.getId())
                .when(chatService)
                .saveChat(any(Long.class), any(ChatRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                chat2.getId()
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_후_비회원_혹은_권한X() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        final String url = "/user/chat/inRoom";
        final ChatErrorResult error = ChatErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChatInRoom(eq(null), any(ChatRoomRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(roomRequest))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_후_채팅방_아이디X() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        final String url = "/user/chat/inRoom";
        final ChatErrorResult error = ChatErrorResult.ROOM_NOT_EXIST;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChatInRoom(any(Long.class), any(ChatRoomRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(roomRequest))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 쪽지_작성_대화방_생성_후_탈퇴한_회원끼리_대화() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        final String url = "/user/chat/inRoom";
        final ChatErrorResult error = ChatErrorResult.WITHDRAWN_MEMBER;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChatInRoom(any(Long.class), any(ChatRoomRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(roomRequest))
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
    public void 쪽지_작성_대화방_생성_후_자신에게_쪽지작성() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        final String url = "/user/chat/inRoom";
        final ChatErrorResult error = ChatErrorResult.NO_SEND_TO_SELF;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .saveChatInRoom(any(Long.class), any(ChatRoomRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(roomRequest))
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
    public void 쪽지_작성_대화방_생성_후_성공() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        final String url = "/user/chat/inRoom";

        Mockito.doReturn(chat1.getId())
                .when(chatService)
                .saveChatInRoom(any(Long.class), any(ChatRoomRequest.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(roomRequest))
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                chat1.getId()
                        ))
                );
    }

    @Test
    public void 나의_쪽지함_조회() throws Exception {
        //given
        ChatListDto chatListDTO = new ChatListDto(chatRoom1);
        List<ChatListDto> dtoList = List.of(chatListDTO);
        Slice<ChatListDto> chatListDTOSlice = new SliceImpl<>(dtoList);

        final String url = "/user/chat/list";

        Mockito.doReturn(chatListDTOSlice)
                .when(chatService)
                .findAll(receiver.getId(), PageRequest.of(0, 10));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", createJwtToken())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 쪽지_자세히_보기() throws Exception {
        //given
        ChatDto.ChatInfo chatInfo1 = new ChatDto.ChatInfo(chat1);
        ChatDto.ChatInfo chatInfo2 = new ChatDto.ChatInfo(chat2);
        ChatDto.ChatInfo chatInfo3 = new ChatDto.ChatInfo(chat3);
        SliceImpl<ChatDto.ChatInfo> chatInfoSlice = new SliceImpl<>(Arrays.asList(chatInfo1, chatInfo2, chatInfo3));
        ChatDto chatDTO = new ChatDto(chatRoom1, chatInfoSlice);

        Mockito.doReturn(chatDTO)
                .when(chatService)
                .findByOpponent(any(), any(), any());

        //when
        final String url = "/user/chat/{room_id}";
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, chatRoom1.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", createJwtToken())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 대화방_삭제_탈퇴한_유저() throws Exception {
        //given
        final String url = "/user/chat/{room_id}";
        ChatErrorResult error = ChatErrorResult.WITHDRAWN_MEMBER;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .deleteChatRoom(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, chatRoom1.getId())
                        .header("Authorization", createJwtToken())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 대화방_삭제_권한없는_유저() throws Exception {
        //given
        final String url = "/user/chat/{room_id}";
        ChatErrorResult error = ChatErrorResult.UNAUTHORIZED_USER_ACCESS;
        Mockito.doThrow(new ChatException(error))
                .when(chatService)
                .deleteChatRoom(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, chatRoom1.getId())
                        .header("Authorization", createJwtToken())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 대화방_삭제_성공() throws Exception {
        //given
        final String url = "/user/chat/{room_id}";
        Mockito.doReturn(chatRoom1.getId())
                .when(chatService)
                .deleteChatRoom(any(), any());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, chatRoom1.getId())
                        .header("Authorization", createJwtToken())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        chatRoom1.getId()
                )));

    }

}