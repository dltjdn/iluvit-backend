package FIS.iLUVit.repository;

import FIS.iLUVit.domain.CommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {

    /*
        유저 id와 댓글 id를 파라미터로 받아서 사용자 및 댓글로 조회합니다.
     */
    @Query("select ch " +
            "from CommentHeart ch " +
            "join fetch ch.comment c " +
            "where ch.user.id = :userId and ch.comment.id = :commentId")
    Optional<CommentHeart> findByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

}
