package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.PostHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.PostErrorResult;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.repository.PostHeartRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("post-heart")
public class PostHeartController {

    private final PostHeartRepository postHeartRepository;
    private final PostService postService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 등록
    */
    @PostMapping("{postId}")
    public Long createPostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.savePostHeart(userId, postId);
    }
    
    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 취소
     * 비고: 기존에 좋아요 눌렀던 상태여야 취소 가능
    */
    @DeleteMapping("{postId}")
    public void deletePostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        PostHeart postHeart = postHeartRepository.findByPostAndUser(userId, postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));
        postHeartRepository.delete(postHeart);
    }

}