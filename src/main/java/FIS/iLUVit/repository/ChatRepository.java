package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Chat;
import FIS.iLUVit.domain.ChatRoom;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long> {

//    @Query("select c from Chat c " +
//            "join fetch c.chatRoom cr " +
//            "where cr.id = :roomId " +
//            "and cr.receiver.id = :userId order by c.createdDate desc ")
//    Slice<Chat> findByChatRoom(@Param("userId") Long userId, @Param("chatroom") ChatRoom chatRoom, Pageable pageable);

    /**
     * Receiver의 채팅방 속 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    Slice<Chat> findByChatRoomAndChatRoomReceiverOrderByCreatedDateDesc(ChatRoom chatRoom, User user, Pageable pageable);

}
