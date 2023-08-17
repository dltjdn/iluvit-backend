package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.PostHeartService;
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
    public ResponseEntity<Void> createPostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        postHeartService.savePostHeart(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /**
     * 게시글 좋아요 취소 ( 기존에 좋아요 눌렀던 상태여야 취소 가능 )
    */
    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePostHeart(@Login Long userId, @PathVariable("postId") Long postId){
        postHeartService.deletePostHeart(userId,postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}