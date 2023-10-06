package FIS.iLUVit.domain.chat.repository;

import FIS.iLUVit.domain.chat.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface ChatRepository extends JpaRepository<Chat, Long> {


    /**
     * 쪽지 수신자의 채팅방 내 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    @Query("SELECT c FROM Chat c " +
            "JOIN FETCH c.chatRoom cr " +
            "WHERE cr.id = :roomId " +
            "AND cr.receiver.id = :userId " +
            "ORDER BY c.createdDate DESC ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId,
                               @Param("roomId") Long roomId,
                               Pageable pageable);

    /**
     * 쪽지 수신자 채팅방 내 채팅 상대방이 차단되기 이전의 채팅을 생성일시를 기준으로 내림차순으로 조회합니다
     */
    @Query("SELECT c FROM Chat c " +
            "JOIN FETCH c.chatRoom cr " +
            "WHERE cr.id = :roomId " +
            "AND cr.receiver.id = :userId " +
            "AND c.createdDate <= :blockedDate " +
            "ORDER BY c.createdDate DESC ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId,
                               @Param("roomId") Long roomId,
                               @Param("blockedDate") LocalDateTime blockedDate,
                               Pageable pageable);
}
