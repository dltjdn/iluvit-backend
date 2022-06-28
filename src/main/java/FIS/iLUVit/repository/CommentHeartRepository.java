package FIS.iLUVit.repository;

import FIS.iLUVit.domain.CommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {
    @Query("select ch from CommentHeart ch where ch.user.id = :userId and ch.comment.id = :commentId")
    Optional<CommentHeart> findByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
