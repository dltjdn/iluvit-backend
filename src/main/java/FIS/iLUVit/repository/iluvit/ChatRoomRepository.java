package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.ChatRoom;
import FIS.iLUVit.domain.iluvit.Post;
import FIS.iLUVit.domain.iluvit.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByReceiverAndSenderAndPost(User receiver, User sender, Post post);

    Optional<ChatRoom> findByReceiverAndSenderAndPostAndAnonymous(User receiver, User sender, Post post, Boolean anonymous);

    @Query("select cr from ChatRoom cr " +
            "left join fetch cr.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center c " +
            "where cr.receiver.id = :userId order by cr.updatedDate desc ")
    Slice<ChatRoom> findByUser(@Param("userId") Long userId, @Param("userId") Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update ChatRoom cr set cr.post.id = null, cr.comment.id = null where cr.post.id in :postIds ")
    Integer setPostIsNull(@Param("postIds") Collection<Long> postIds);
}
