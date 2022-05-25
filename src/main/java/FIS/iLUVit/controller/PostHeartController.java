package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.PostHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.PostHeartRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostHeartController {

    private final PostHeartRepository postHeartRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @PostMapping("/postHeart/post/{post_id}")
    public void like(@Login Long userId, @PathVariable("post_id") Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글"));
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
        findPost.getPostHearts().forEach(p -> {
            if (p.getUser().getId() == userId) {
                throw new IllegalStateException("이미 좋아요 누른 게시글");
            }
        });
        PostHeart postHeart = new PostHeart(findUser, findPost);
        postHeartRepository.save(postHeart);
    }

    @DeleteMapping("/postHeart/post/{post_id}")
    public void cancel(@Login Long userId, @PathVariable("post_id") Long postId) {
        PostHeart postHeart = postHeartRepository.findByPostAndUser(userId, postId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 좋아요"));
        postHeartRepository.delete(postHeart);
    }
}
