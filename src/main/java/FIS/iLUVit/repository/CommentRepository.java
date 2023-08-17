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
        댓글 사용자 id로 댓글을 조회합니다.
     */
    @Query("select c from Comment c " +
            "left join fetch c.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center ct " +
            "where c.user.id = :userId")
    Slice<Comment> findByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        게시글과 사용자와 익명여부로 첫 댓글을 조회합니다;
     */
    Optional<Comment> findFirstByPostAndUserAndAnonymous(Post post, User user, Boolean anonymous);

    /*
        댓글 id로 댓글 리스트를 조회합니다.
     */
    @Query("select c from Comment c " +
            "where c.post.id = :postId ")
    List<Comment> findByPostId(@Param("postId") Long postId);
}
