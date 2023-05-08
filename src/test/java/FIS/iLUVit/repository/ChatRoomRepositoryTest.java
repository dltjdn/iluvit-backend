package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ChatRoomRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

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

    @BeforeEach
    public void init() {
        receiver = Parent.builder()
                .name("나")
                .auth(Auth.PARENT)
                .build();

        sender = Parent.builder()
                .name("상대방")
                .auth(Auth.PARENT)
                .build();

        board1 = CreateTest.createBoard("자유게시판", BoardKind.NORMAL, null, true);

        post1 = Creator.createPost("제목", "내용", true, board1, receiver);

        chatRoom1 = Creator.createChatRoom(receiver, sender, post1);
        chatRoom2 = Creator.createChatRoom(sender, receiver, post1);

        chat1 = Creator.createChat("안녕하세요", chatRoom1, sender, receiver);
        chat2 = Creator.createChat("안녕하세요", chatRoom2, receiver, sender);
        chat4 = Creator.createChat("감사해요", chatRoom2, receiver, sender);
        chat3 = Creator.createChat( "감사해요", chatRoom1, sender, receiver);
        chat5 = Creator.createChat( "잘있어요", chatRoom1, sender, receiver);
        chat6 = Creator.createChat( "잘있어요", chatRoom2, receiver, sender);

        em.persist(receiver);
        em.persist(sender);
        em.persist(board1);
        em.persist(post1);
        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(chat1);
        em.persist(chat2);
        em.persist(chat3);
        em.persist(chat4);
        em.persist(chat5);
        em.persist(chat6);
        em.flush();
        em.clear();
    }

    @Test
    public void 내정보_상대방정보_게시글로_채팅방찾기() throws Exception {
        //given

        //when
        ChatRoom findChatRoom = chatRoomRepository
                .findByReceiverAndSenderAndPost(receiver, sender, post1).orElse(null);

        ChatRoom myChatRoom = chatRoomRepository.findById(chatRoom1.getId()).orElse(null);
        //then
        assertThat(findChatRoom).isEqualTo(myChatRoom);
        assertThat(findChatRoom.getReceiver().getId()).isEqualTo(receiver.getId());
    }

    @Test
    public void 내정보로_채팅방찾기() throws Exception {
        //given

        //when
        Slice<ChatRoom> chatRoomSlice = chatRoomRepository
                .findByUser(receiver.getId(), PageRequest.of(0, 10));

        ChatRoom findChatRoom = chatRoomSlice.getContent().get(0);
        ChatRoom myChatRoom = chatRoomRepository.findById(chatRoom1.getId()).orElse(null);
        //then
        assertThat(findChatRoom).isEqualTo(myChatRoom);
        assertThat(findChatRoom.getReceiver().getId()).isEqualTo(receiver.getId());
    }

    @Test
    public void Post_null값으로_업데이트() throws Exception {
        //given

        //when
        List<Long> postIds = List.of(post1.getId());
        Integer value = chatRoomRepository.setPostIsNull(postIds);
        List<ChatRoom> all = chatRoomRepository.findAll();
        //then

        assertThat(value).isEqualTo(2);
        all.forEach(cr -> {
            assertThat(cr.getPost()).isNull();
        });
    }

    @Test
    public void 내정보_상대방정보_게시글_익명정보로_채팅방찾기() throws Exception {
        //given

        //when
        ChatRoom findRoom1 = chatRoomRepository
                .findByReceiverAndSenderAndPostAndAnonymous(receiver, sender, post1, true)
                .orElse(null);

        ChatRoom findRoom2 = chatRoomRepository
                .findByReceiverAndSenderAndPostAndAnonymous(receiver, sender, post1, false)
                .orElse(null);
        //then
        assertThat(findRoom1.getId())
                .isEqualTo(chatRoom1.getId());

        assertThat(findRoom2).isNull();
    }

}