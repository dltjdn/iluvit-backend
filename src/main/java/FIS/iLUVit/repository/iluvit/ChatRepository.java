package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c " +
            "join fetch c.chatRoom cr " +
            "where cr.id = :roomId " +
            "and cr.receiver.id = :userId order by c.createdDate desc ")
    Slice<Chat> findByChatRoom(@Param("userId") Long userId, @Param("roomId") Long roomId, Pageable pageable);

}
