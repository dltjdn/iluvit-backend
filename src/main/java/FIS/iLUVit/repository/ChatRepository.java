package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    /*
        사용자 id와 채팅방 id를 파라미터로 받아서 채팅방으로 조회합니다.
     */
    @Query("select c from Chat c " +
            "join fetch c.chatRoom cr " +
            "where cr.id = :roomId " +
            "and cr.receiver.id = :userId order by c.createdDate desc ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId, @Param("roomId") Long roomId, Pageable pageable);

}
