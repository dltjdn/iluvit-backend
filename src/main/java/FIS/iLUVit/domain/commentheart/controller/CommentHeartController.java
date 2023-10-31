package FIS.iLUVit.domain.commentheart.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.commentheart.service.CommentHeartService;
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
    public ResponseEntity<Long> createCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        Long response = commentHeartService.saveCommentHeart(userId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 좋아요 취소
     */
    @DeleteMapping("{commentId}")
    public ResponseEntity<Long> deleteCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        Long response = commentHeartService.deleteCommentHeart(userId, commentId);
        return ResponseEntity.ok().body(response);
    }
}
