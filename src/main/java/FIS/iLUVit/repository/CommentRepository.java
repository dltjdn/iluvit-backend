package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /*
        유저 id를 파라미터로 받아서 사용자로 댓글을 조회합니다.
     */
    @Query("select c from Comment c " +
            "left join fetch c.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center ct " +
            "where c.user.id = :userId")
    Slice<Comment> findByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        게시글 및 사용자 및 익명으로 댓글을 먼저 조회합니다.
     */
    Optional<Comment> findFirstByPostAndUserAndAnonymous(Post post, User user, Boolean anonymous);

    /*
        게시글 id를 파라미터로 받아서 게시글 Id로 댓글을 조회합니다.
     */
    @Query("select c from Comment c " +
            "where c.post.id =:postId ")
    List<Comment> findByPostId(@Param("postId") Long postId);
}
