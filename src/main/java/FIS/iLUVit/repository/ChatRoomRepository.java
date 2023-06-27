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
        수신자와 발신자와 게시글 별로 채팅방을 조회합니다.
     */
    Optional<ChatRoom> findByReceiverAndSenderAndPost(User receiver, User sender, Post post);

    /*
        수신자와 발신자와 게시글과 익명여부로 채팅방을 조회합니다.
     */
    Optional<ChatRoom> findByReceiverAndSenderAndPostAndAnonymous(User receiver, User sender, Post post, Boolean anonymous);

    /*
        채팅방 수정일을 내림차순으로 정렬하여 채팅방 수신자 id별로 채팅방을 조회합니다.
     */
    @Query("select cr from ChatRoom cr " +
            "left join fetch cr.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center c " +
            "where cr.receiver.id = :userId order by cr.updatedDate desc ")
    Slice<ChatRoom> findByUser(@Param("userId") Long userId, @Param("userId") Pageable pageable);

    /*
        채팅방에 있는 게시글 id별로 게시글 id와 댓글 id를 null로 수정합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update ChatRoom cr set cr.post.id = null, cr.comment.id = null where cr.post.id in :postIds ")
    Integer setPostIsNull(@Param("postIds") Collection<Long> postIds);
}
