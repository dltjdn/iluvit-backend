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

import java.time.LocalDateTime;
import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Receiver의 채팅방 속 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    Slice<Chat> findByChatRoomAndChatRoomReceiverOrderByCreatedDateDesc(ChatRoom chatRoom, User user, Pageable pageable);

    /**
     * 쪽지 수신자의 채팅방 내 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    @Query("select c from Chat c " +
            "join fetch c.chatRoom cr " +
            "where cr.id = :roomId " +
            "and cr.receiver.id = :userId " +
            "order by c.createdDate desc ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId,
                               @Param("roomId") Long roomId,
                               Pageable pageable);

    /**
     * 쪽지 수신자 채팅방 내 채팅 상대방이 차단되기 이전의 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    @Query("select c from Chat c " +
            "join fetch c.chatRoom cr " +
            "where cr.id = :roomId " +
            "and cr.receiver.id = :userId " +
            "and c.createdDate <= :blockedDate " +
            "order by c.createdDate desc ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId,
                               @Param("roomId") Long roomId,
                               @Param("blockedDate") LocalDateTime blockedDate,
                               Pageable pageable);
}
