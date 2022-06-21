package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.CommentDTO;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.service.CommentHeartService;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentHeartService commentHeartService;

    @PostMapping("/comment")
    public void registerComment(@Login Long userId,
                                @RequestParam("post_id") Long postId,
                                @RequestParam(value = "comment_id", required = false) Long commentId,
                                @RequestBody RegisterCommentRequest request) {

        commentService.registerComment(userId, postId, commentId, request);
    }

    @PatchMapping("/comment")
    public void deleteComment(@Login Long userId,
                              @RequestParam("comment_id") Long commentId) {
        commentService.deleteComment(userId, commentId);
        // 삭제하면 그 대댓글까지 삭제? or 대댓글은 남김?
    }

    @PostMapping("/commentHeart/comment/{comment_id}")
    public void like(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.save(userId, comment_id);
    }

    @DeleteMapping("/commentHeart/comment/{comment_id}")
    public void cancel(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.delete(userId, comment_id);
    }

    @GetMapping("/comment/mypage")
    public Slice<CommentDTO> searchCommentByUser(@Login Long userId) {
        return commentService.searchByUser(userId);
    }
}
