package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.CommentHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("comment")
public class CommentHeartController {

    private final CommentHeartService commentHeartService;

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 10:16 AM
     내용: 댓글 좋아요
     */
    @PostMapping("comment-heart/{commentId}")
    public Long like(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.save(userId, commentId);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 10:18 AM
     내용: 댓글 좋아요 취소
     */
    @DeleteMapping("comment-heart/{commentId}")
    public Long cancel(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.delete(userId, commentId);
    }
}
