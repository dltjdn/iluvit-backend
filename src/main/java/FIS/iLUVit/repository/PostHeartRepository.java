package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.PostHeart;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    /**
     * 해당 유저와 게시물에 해당하는 게시물 좋아요를 조회합니다
     */
    Optional<PostHeart> findByUserAndPost(User user, Post post);
}
