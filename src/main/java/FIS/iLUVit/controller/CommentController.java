package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.service.CommentHeartService;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentHeartService commentHeartService;

    @PostMapping("/comment")
    public void registerComment(@Login Long userId,
                                @RequestParam("post_id") Long postId,
                                @RequestParam("comment_id") Long commentId,
                                @RequestBody RegisterCommentRequest request) {

        commentService.registerComment(userId, postId, commentId, request);
    }

    @PatchMapping("/comment")
    public void deleteComment(@Login Long userId,
                              @RequestParam("comment_id") Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @PostMapping("/commentHeart/comment/{comment_id}")
    public void like(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.save(userId, comment_id);
    }

    @DeleteMapping("/commentHeart/comment/{comment_id}")
    public void cancel(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.delete(userId, comment_id);
    }
}
