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

    /**
     * 해당 사용자의 댓글 리스트를 조회합니다
     */
    Slice<Comment> findByUser(User user, Pageable pageable);

    /**
     * 게시글과 사용자, 익명여부로 댓글을 조회합니다
     */
    Optional<Comment> findFirstByPostAndUserAndAnonymous(Post post, User user, Boolean anonymous);

    /**
     * 게시물 별 댓글 리스트를 조회합니다
     */
    List<Comment> findByPost(Post post);

    List<Comment> findByPostAndParentCommentIsNull(Post post);
    List<Comment>  findByParentComment(Comment comment);
}
