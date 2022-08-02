package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.controller.dto.CreateChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.ChatAlarm;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ImageService imageService;

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;

    ObjectMapper objectMapper;

    User receiver;
    User sender;

    Board board1;

    Post post1;
    Post post2;
    Comment comment1;
    Comment comment2;

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
        post2 = Creator.createPost(40L, "제목22", "내용22", true, board1, sender);
        comment1 = Creator.createComment(13L, true, "asdf", post1, receiver);
        comment2 = Creator.createComment(130L, true, "asdf22", post1, sender);

        chatRoom1 = Creator.createChatRoom(5L, receiver, sender, post1);
        chatRoom2 = Creator.createChatRoom(6L, sender, receiver, post1);

        chat1 = Creator.createChat(7L,"안녕하세요", chatRoom1, sender, receiver);
        chat2 = Creator.createChat(8L,"안녕하세요", chatRoom2, sender, receiver);
        chat3 = Creator.createChat(9L,"감사해요", chatRoom1, receiver, sender);
        chat4 = Creator.createChat(10L, "감사해요", chatRoom2, receiver, sender);
        chat5 = Creator.createChat(11L, "잘있어요", chatRoom1, sender, receiver);
        chat6 = Creator.createChat(12L, "잘있어요", chatRoom2, sender, receiver);

    }

    @Test
    public void 쪽지_작성_비회원() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChat(null, request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 쪽지_작성_발신자X() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());

        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(receiver.getId());
        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChat(receiver.getId(), request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 쪽지_작성_게시글X() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        request.setComment_id(comment1.getId());

        Mockito.doReturn(Optional.of(receiver))
                .when(userRepository)
                .findById(receiver.getId());

        Mockito.doReturn(Optional.empty())
                .when(postRepository)
                .findById(post1.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChat(receiver.getId(), request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.POST_NOT_EXIST);
    }

    @Test
    public void 쪽지_작성_댓글X() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        request.setComment_id(comment1.getId());

        Mockito.doReturn(Optional.of(receiver))
                .when(userRepository)
                .findById(receiver.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());

        Mockito.doReturn(Optional.empty())
                .when(commentRepository)
                .findById(comment1.getId());

        //when
        CommentException result = assertThrows(CommentException.class,
                () -> chatService.saveChat(receiver.getId(), request));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(CommentErrorResult.NO_EXIST_COMMENT);
    }

    @Test
    public void 쪽지_작성_댓글O_자신에게_보냄() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());
        request.setComment_id(comment1.getId());

        Mockito.doReturn(Optional.of(receiver))
                .when(userRepository)
                .findById(receiver.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());

        Mockito.doReturn(Optional.of(comment1))
                .when(commentRepository)
                .findById(comment1.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChat(receiver.getId(), request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.NO_SEND_TO_SELF);
    }

    @Test
    public void 쪽지_작성_댓글X_자신에게_보냄() throws Exception {
        //given
        request.setMessage("안녕");
        request.setPost_id(post1.getId());

        Mockito.doReturn(Optional.of(receiver))
                .when(userRepository)
                .findById(receiver.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChat(receiver.getId(), request));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.NO_SEND_TO_SELF);
    }

    @Test
    public void 쪽지_작성_댓글X_성공() throws Exception {
        //given
        try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {

            request.setMessage("안녕");
            request.setPost_id(post2.getId());

            Mockito.doReturn(Optional.of(receiver))
                    .when(userRepository)
                    .findById(receiver.getId());

            Mockito.doReturn(Optional.of(post2))
                    .when(postRepository)
                    .findById(post2.getId());

            Mockito.doReturn(chat2)
                    .when(chatRepository)
                    .save(any());

            alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                    .thenReturn("회원 {0}로 부터 새로운 채팅을 받았어요");

            AlarmEvent alarmEvent = new AlarmEvent(new ChatAlarm(receiver, sender, false));
            alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                    .thenReturn(alarmEvent);

            //when
            Long returnedId = chatService.saveChat(receiver.getId(), request);
            //then
            assertThat(returnedId).isEqualTo(chat2.getId());
        }
    }

    @Test
    public void 쪽지_작성_댓글O_성공() throws Exception {
        //given
        try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {

            request.setMessage("안녕");
            request.setPost_id(post2.getId());
            request.setComment_id(comment2.getId());

            Mockito.doReturn(Optional.of(receiver))
                    .when(userRepository)
                    .findById(receiver.getId());

            Mockito.doReturn(Optional.of(post2))
                    .when(postRepository)
                    .findById(post2.getId());

            Mockito.doReturn(Optional.of(comment2))
                    .when(commentRepository)
                    .findById(comment2.getId());

            Mockito.doReturn(chat2)
                    .when(chatRepository)
                    .save(any());

            alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                    .thenReturn("회원 {0}로 부터 새로운 채팅을 받았어요");

            AlarmEvent alarmEvent = new AlarmEvent(new ChatAlarm(receiver, sender, false));
            alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(any(Alarm.class)))
                    .thenReturn(alarmEvent);

            //when
            Long returnedId = chatService.saveChat(receiver.getId(), request);
            //then
            assertThat(returnedId).isEqualTo(chat2.getId());
        }
    }

    @Test
    public void 대화방_목록_조회() throws Exception {
        //given
        List<ChatRoom> chatRooms = Arrays.asList(chatRoom1);
        Mockito.doReturn(new SliceImpl<>(chatRooms))
                .when(chatRoomRepository)
                .findByUser(receiver.getId(), PageRequest.of(0, 10));

        ImageServiceStubAmazon stubAmazon = new ImageServiceStubAmazon();

        String profileImagePath = receiver.getProfileImagePath();
        System.out.println("profileImagePath = " + profileImagePath);
        Mockito.doReturn(stubAmazon.getProfileImage(receiver))
                .when(imageService)
                .getProfileImage(any(BaseImageEntity.class));

        //when
        Slice<ChatListDTO> all = chatService
                .findAll(receiver.getId(), PageRequest.of(0, 10));
        //then
        List<ChatListDTO> content = all.getContent();
        assertThat(content.size()).isEqualTo(1);
        ChatListDTO chatListDTO = content.get(0);
        assertThat(chatListDTO.getRoom_id()).isEqualTo(chatRoom1.getId());
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_채팅방_아이디X() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.empty())
                .when(chatRoomRepository)
                .findById(chatRoom1.getId());
        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(receiver.getId(), roomRequest));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.ROOM_NOT_EXIST);
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_채팅방_탈퇴한_회원() throws Exception {
        //given
        ChatRoom chatRoom3 = Creator.createChatRoom(999L, null, sender, post1);

        roomRequest.setRoom_id(chatRoom3.getId());
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(receiver.getId(), roomRequest));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.WITHDRAWN_MEMBER);
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_채팅방_탈퇴한_상대방() throws Exception {
        //given
        ChatRoom chatRoom3 = Creator.createChatRoom(999L, receiver, null, post1);

        roomRequest.setRoom_id(chatRoom3.getId());
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(receiver.getId(), roomRequest));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.WITHDRAWN_MEMBER);
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_채팅방_유저아님_권한X() throws Exception {
        //given
        roomRequest.setRoom_id(chatRoom1.getId());
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom1))
                .when(chatRoomRepository)
                .findById(chatRoom1.getId());
        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(999L, roomRequest));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_자신에게_보냄() throws Exception {
        //given
        ChatRoom chatRoom3 = Creator.createChatRoom(999L, receiver, receiver, post1);

        roomRequest.setRoom_id(999L);
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());
        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(receiver.getId(), roomRequest));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.NO_SEND_TO_SELF);
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_상대방_채팅방_없으면_다시생성() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(receiver)
                .sender(sender)
                .post(post1)
                .build();

        roomRequest.setRoom_id(999L);
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        Mockito.doReturn(receiver)
                .when(userRepository)
                .getById(receiver.getId());

        Mockito.doReturn(sender)
                .when(userRepository)
                .getById(sender.getId());

        Mockito.doReturn(chat1)
                .when(chatRepository)
                .save(any());

        //when
        Long chatId = chatService.saveChatInRoom(receiver.getId(), roomRequest);
        //then
        assertThat(chatId).isEqualTo(chat1.getId());
    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_상대방_채팅방_조회_채팅방_아이디X() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(receiver)
                .sender(sender)
                .post(post1)
                .partner_id(1000L)
                .build();

        roomRequest.setRoom_id(999L);
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        Mockito.doReturn(receiver)
                .when(userRepository)
                .getById(receiver.getId());

        Mockito.doReturn(sender)
                .when(userRepository)
                .getById(sender.getId());

        Mockito.doReturn(Optional.empty())
                .when(chatRoomRepository)
                .findById(chatRoom3.getPartner_id());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.saveChatInRoom(receiver.getId(), roomRequest));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.ROOM_NOT_EXIST);

    }

    @Test
    public void 채팅방아이디로_대화방내_쪽지작성_상대방_채팅방_조회_성공() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(receiver)
                .sender(sender)
                .post(post1)
                .partner_id(1000L)
                .build();

        ChatRoom chatRoom4 = ChatRoom.builder()
                .id(1000L)
                .chatList(new ArrayList<>())
                .receiver(sender)
                .sender(receiver)
                .post(post1)
                .partner_id(999L)
                .build();

        roomRequest.setRoom_id(999L);
        roomRequest.setMessage("안녕");

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        Mockito.doReturn(receiver)
                .when(userRepository)
                .getById(receiver.getId());

        Mockito.doReturn(sender)
                .when(userRepository)
                .getById(sender.getId());

        Mockito.doReturn(Optional.of(chatRoom4))
                .when(chatRoomRepository)
                .findById(chatRoom3.getPartner_id());

        Mockito.doReturn(chat1)
                .when(chatRepository)
                .save(any());

        //when
        Long chatId = chatService.saveChatInRoom(receiver.getId(), roomRequest);
        //then
        assertThat(chatId).isEqualTo(chat1.getId());
    }

    @Test
    public void 채팅방_상대방과의_대화목록_조회_채팅방아이디X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(chatRoomRepository)
                .findById(chatRoom1.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.findByOpponent(receiver.getId(), chatRoom1.getId(),
                        PageRequest.of(0, 10)));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.ROOM_NOT_EXIST);
    }

    @Test
    public void 채팅방_상대방과의_대화목록_조회_성공() throws Exception {
        //given
        ChatDTO.ChatInfo chatInfo1 = new ChatDTO.ChatInfo(chat1);
        ChatDTO.ChatInfo chatInfo2 = new ChatDTO.ChatInfo(chat3);
        ChatDTO.ChatInfo chatInfo3 = new ChatDTO.ChatInfo(chat5);
        List<ChatDTO.ChatInfo> chatInfos = Arrays.asList(chatInfo1, chatInfo2, chatInfo3);
        SliceImpl<ChatDTO.ChatInfo> chatInfoSlice = new SliceImpl<>(chatInfos);
        ChatDTO givenChatDTO = new ChatDTO(chatRoom1, chatInfoSlice);

        chatRoom1.updatePartnerId(chatRoom2.getId());
        chatRoom2.updatePartnerId(chatRoom1.getId());

        Mockito.doReturn(Optional.of(chatRoom1))
                .when(chatRoomRepository)
                .findById(chatRoom1.getId());

        SliceImpl<Chat> chats = new SliceImpl<>(Arrays.asList(chat1, chat3, chat5));

        Mockito.doReturn(chats)
                .when(chatRepository)
                .findByChatRoom(receiver.getId(), chatRoom1.getId(), PageRequest.of(0, 10));

        //when
        ChatDTO chatDTO = chatService.findByOpponent(receiver.getId(), chatRoom1.getId(),
                PageRequest.of(0, 10));
        //then
        String actual = objectMapper.writeValueAsString(chatDTO);
        System.out.println("actual = " + actual);
        String expected = objectMapper.writeValueAsString(givenChatDTO);
        System.out.println("expected = " + expected);
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    public void 채팅방_삭제_채팅방에_속한_유저_탈퇴한_회원() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(null)
                .sender(sender)
                .post(post1)
                .partner_id(1000L)
                .build();

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.deleteChatRoom(999L, chatRoom3.getId()));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.WITHDRAWN_MEMBER);
    }

    @Test
    public void 채팅방_삭제_채팅방에_속한_유저X() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(receiver)
                .sender(sender)
                .post(post1)
                .partner_id(1000L)
                .build();

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        //when
        ChatException result = assertThrows(ChatException.class,
                () -> chatService.deleteChatRoom(9999L, chatRoom3.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 채팅방_삭제_성공() throws Exception {
        //given
        ChatRoom chatRoom3 = ChatRoom.builder()
                .id(999L)
                .chatList(new ArrayList<>())
                .receiver(receiver)
                .sender(sender)
                .post(post1)
                .partner_id(1000L)
                .build();

        ChatRoom chatRoom4 = ChatRoom.builder()
                .id(1000L)
                .chatList(new ArrayList<>())
                .receiver(sender)
                .sender(receiver)
                .post(post1)
                .partner_id(999L)
                .build();

        Mockito.doReturn(Optional.of(chatRoom3))
                .when(chatRoomRepository)
                .findById(chatRoom3.getId());

        Mockito.doReturn(Optional.of(chatRoom4))
                .when(chatRoomRepository)
                .findById(chatRoom3.getPartner_id());

        Mockito.doNothing()
                .when(chatRoomRepository)
                .deleteById(chatRoom3.getId());

        //when
        Long removedId = chatService.deleteChatRoom(receiver.getId(), chatRoom3.getId());

        //then
        assertThat(removedId).isEqualTo(chatRoom3.getId());
        assertThat(chatRoom4.getPartner_id()).isNull();

    }
}
