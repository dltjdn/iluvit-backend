package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Chat;
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

    /*
        채팅방 생성일을 내림차순으로 정렬하고 채팅방 id와 수신자 id별로 대화를 조회합니다.
     */
    @Query("select c from Chat c " +
            "join fetch c.chatRoom cr " +
            "where cr.id = :roomId " +
            "and cr.receiver.id = :userId " +
            "order by c.createdDate desc ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId,
                               @Param("roomId") Long roomId,
                               Pageable pageable);

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
