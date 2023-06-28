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
     * 작성자: 이창윤
     * 작성내용: 댓글 좋아요 등록
     */
    @PostMapping("{commentId}")
    public Long createCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.saveCommentHeart(userId, commentId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 댓글 좋아요 취소
     */
    @DeleteMapping("{commentId}")
    public Long deleteCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.deleteCommentHeart(userId, commentId);
    }

}
