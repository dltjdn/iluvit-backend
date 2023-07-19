package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.CommentHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("comment-heart")
public class CommentHeartController {
    private final CommentHeartService commentHeartService;

    /**
     * COMMON
     */

    /**
     * 댓글 좋아요 등록
     */
    @PostMapping("{commentId}")
    public ResponseEntity<Void> createCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        commentHeartService.saveCommentHeart(userId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 댓글 좋아요 취소
     */
    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> deleteCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        commentHeartService.deleteCommentHeart(userId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
