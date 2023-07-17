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

    /**
     * Receiver, Sender, 게시물, 익명 여부에 일치하는 채팅방을 조회합니다
     */
    Optional<ChatRoom> findByReceiverAndSenderAndPostAndAnonymous(User receiver, User sender, Post post, Boolean anonymous);

    /**
     * Receiver의 채팅방들을 수정일시를 기준으로 내림차순으로 조회합니다
     */
    Slice<ChatRoom> findByReceiverOrderByUpdatedDateDesc(User receiver, Pageable pageable);


    /**
     * @@@ postservcie 건드릴 때 사라질수도
     * 채팅방에서 주어진 게시물 아이디들과 일치하는 게시물이나 댓글을 null로 만듭니다
     */
    @Modifying(clearAutomatically = true)
    @Query("update ChatRoom cr set cr.post.id = null, cr.comment.id = null where cr.post.id in :postIds ")
    void setPostIsNull(@Param("postIds") Collection<Long> postIds);
}
