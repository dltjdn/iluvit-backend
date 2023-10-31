package FIS.iLUVit.domain.postheart.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.postheart.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 게시글 좋아요 등록
    */
    @PostMapping("{postId}")
    public ResponseEntity<Long> createPostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        Long response = postHeartService.savePostHeart(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 게시글 좋아요 취소 ( 기존에 좋아요 눌렀던 상태여야 취소 가능 )
    */
    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePostHeart(@Login Long userId, @PathVariable("postId") Long postId){
        postHeartService.deletePostHeart(userId,postId);
        return ResponseEntity.noContent().build();
    }

}