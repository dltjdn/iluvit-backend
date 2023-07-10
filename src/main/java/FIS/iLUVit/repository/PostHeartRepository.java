package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    /*
        샤용자 id와 게시글 id로 게시글 하트를 조회합니다.
     */
    @Query("select ph from PostHeart ph join ph.post p where ph.user.id = :userId and p.id = :postId")
    Optional<PostHeart> findByPostAndUser(@Param("userId") Long userId, @Param("postId") Long postId);
}
