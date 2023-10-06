package FIS.iLUVit.domain.commentheart.repository;

import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.commentheart.domain.CommentHeart;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {

   /**
    *  해당 유저와 댓글에 대한 댓글 좋아요를 조회한다
    */
    Optional<CommentHeart> findByUserAndComment(User user, Comment comment);

}
