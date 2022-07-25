package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c " +
            "left join fetch c.post p " +
            "left join fetch p.board b " +
            "left join fetch b.center ct " +
            "where c.user.id = :userId")
    Slice<Comment> findByUser(@Param("userId") Long userId, Pageable pageable);
}
