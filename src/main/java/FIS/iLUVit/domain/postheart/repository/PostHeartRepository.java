package FIS.iLUVit.domain.postheart.repository;

import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.postheart.domain.PostHeart;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    /**
     * 해당 유저와 게시물에 해당하는 게시물 좋아요를 조회합니다
     */
    Optional<PostHeart> findByUserAndPost(User user, Post post);
}
