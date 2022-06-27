package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.PostHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.repository.PostHeartRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostHeartController {

    private final PostHeartRepository postHeartRepository;
    private final PostService postService;

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:35 PM
        내용: 게시글 좋아요
    */
    @PostMapping("/postHeart/post/{post_id}")
    public void like(@Login Long userId, @PathVariable("post_id") Long postId) {
        postService.savePostHeart(userId, postId);
    }
    
    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:39 PM
        내용: 게시글 좋아요 취소, 기존에 좋아요 눌렀던 상태여야 취소 가능
    */
    @DeleteMapping("/postHeart/post/{post_id}")
    public void cancel(@Login Long userId, @PathVariable("post_id") Long postId) {
        PostHeart postHeart = postHeartRepository.findByPostAndUser(userId, postId)
                .orElseThrow(() -> new PostException("존재하지 않는 좋아요"));
        postHeartRepository.delete(postHeart);
    }
}
