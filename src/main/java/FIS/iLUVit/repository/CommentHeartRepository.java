package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.CommentHeart;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {

   /**
    *  해당 유저와 댓글에 대한 댓글 좋아요를 조회한다
    */
    Optional<CommentHeart> findByUserAndComment(User user, Comment comment);

}
