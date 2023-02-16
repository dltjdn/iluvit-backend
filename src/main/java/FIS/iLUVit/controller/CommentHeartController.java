package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.CommentHeartService;
import lombok.RequiredArgsConstructor;
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
     작성자: 이창윤
     내용: 댓글 좋아요
     */
    @PostMapping("{commentId}")
    public Long like(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.save(userId, commentId);
    }

    /**
     작성자: 이창윤
     내용: 댓글 좋아요 취소
     */
    @DeleteMapping("{commentId}")
    public Long cancel(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.delete(userId, commentId);
    }

}
