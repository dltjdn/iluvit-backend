package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ChatRoom;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /*
        수신자 및 발신자 및 우편으로 조회합니다.
     */
    Optional<ChatRoom> findByReceiverAndSenderAndPost(User receiver, User sender, Post post);

    /*
        수신자 및 발신자 및 게시물 및 익명으로 조회합니다.
     */
    Optional<ChatRoom> findByReceiverAndSenderAndPostAndAnonymous(User receiver, User sender, Post post, Boolean anonymous);

    /*
        (자기자신)유저 id와 (상대방)유저 id를 파라미터로 받아서 유저로 채팅방을 조회합니다.
     */
    @Query("select cr from ChatRoom cr " +
            "left join fetch cr.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center c " +
            "where cr.receiver.id = :userId order by cr.updatedDate desc ")
    Slice<ChatRoom> findByUser(@Param("userId") Long userId, @Param("userId") Pageable pageable);

    /*
        여러 개의 게시글 id를 파라미터로 받아서 게시글 id와 댓글 id를 null로 지정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ChatRoom cr set cr.post.id = null, cr.comment.id = null where cr.post.id in :postIds ")
    Integer setPostIsNull(@Param("postIds") Collection<Long> postIds);
}
