package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.BoardKind;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.repository.iluvit.ChatRepository;
import FIS.iLUVit.service.createmethod.CreateTest;
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

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ChatRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ChatRepository chatRepository;

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
    public void 나의_채팅방내_채팅목록() throws Exception {
        //given

        //when
        Slice<Chat> chatSlice = chatRepository
                .findByChatRoom(receiver.getId(), chatRoom1.getId(), PageRequest.of(0, 10));
        List<Chat> chatList = chatSlice.getContent();
        //then
        assertThat(chatList.size()).isEqualTo(3);
        assertThat(chatList).extracting("message")
                .containsOnly("안녕하세요", "감사해요", "잘있어요");
        ChatRoom chatRoom = chatList.get(0).getChatRoom();
        assertThat(chatRoom.getReceiver().getId()).isEqualTo(receiver.getId());
    }

}