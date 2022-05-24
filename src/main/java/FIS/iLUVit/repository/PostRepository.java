package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p left join fetch p.user u left join fetch p.board b " +
            "left join fetch b.center c where p.id = :postId")
    Optional<Post> findByIdWithUserAndBoardAndCenter(@Param("postId") Long postId);
}
