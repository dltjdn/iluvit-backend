package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c join fetch c.post p join fetch p.board b left join fetch b.center ct where c.id in " +
            "(select max(c.id) from Chat c where c.receiver.id = :userId or c.sender.id = :userId group by c.post.id) " +
            "order by c.createdDate desc ")
    Slice<Chat> findFirstByPost(@Param("userId") Long userId, Pageable pageable);

    @Query("select c from Chat c join fetch c.post p join fetch p.board b left join fetch b.center ct " +
            "where (c.receiver.id = :userId or c.sender.id = :userId) and p.id = :postId order by c.createdDate desc ")
    Slice<Chat> findByPost(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);

    @Query("select c from Chat c where c.receiver.id = :userId or c.sender.id = :userId order by c.createdDate desc ")
    Slice<Chat> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("select c from Chat c where (c.receiver.id = :otherId and c.sender.id = :userId) " +
            "or (c.sender.id = :otherId and c.receiver.id = :userId) order by c.createdDate desc")
    Slice<Chat> findByOpponent(@Param("userId") Long userId, @Param("otherId") Long otherId, Pageable pageable);

}
