package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public void registerComment(@Login Long userId, @RequestParam("post_id") Long postId,
                                @RequestBody RegisterCommentRequest request) {
        commentService.registerComment(userId, postId, request);
    }
}
