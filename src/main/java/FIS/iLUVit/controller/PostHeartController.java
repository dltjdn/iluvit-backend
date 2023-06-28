package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("post-heart")
public class PostHeartController {

    private final PostHeartService postHeartService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 등록
    */
    @PostMapping("{postId}")
    public Long createPostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        return postHeartService.savePostHeart(userId, postId);
    }
    
    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 취소
     * 비고: 기존에 좋아요 눌렀던 상태여야 취소 가능
    */
    @DeleteMapping("{postId}")
    public void deletePostHeart(@Login Long userId, @PathVariable("postId") Long postId){
        postHeartService.deletePostHeart(userId,postId);
    }

}