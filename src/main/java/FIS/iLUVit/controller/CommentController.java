package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.CommentDTO;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.service.CommentHeartService;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentHeartService commentHeartService;

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 10:09 AM
        내용: 댓글 작성, comment_id 값까지 보내는 경우 대댓글 작성
    */
    @PostMapping("/comment")
    public void registerComment(@Login Long userId,
                                @RequestParam("post_id") Long postId,
                                @RequestParam(value = "comment_id", required = false) Long commentId,
                                @RequestBody RegisterCommentRequest request) {

        commentService.registerComment(userId, postId, commentId, request);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 10:14 AM
        내용: 댓글 삭제, 댓글 데이터 지우지 않고 내용(content), 작성자(user)만 null로 변경
    */
    @PatchMapping("/comment")
    public void deleteComment(@Login Long userId,
                              @RequestParam("comment_id") Long commentId) {
        commentService.deleteComment(userId, commentId);
        // 삭제하면 그 대댓글까지 삭제? or 대댓글은 남김?
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 10:16 AM
        내용: 댓글 좋아요
    */
    @PostMapping("/commentHeart/comment/{comment_id}")
    public void like(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.save(userId, comment_id);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 10:18 AM
        내용: 댓글 좋아요 취소
    */
    @DeleteMapping("/commentHeart/comment/{comment_id}")
    public void cancel(@Login Long userId, @PathVariable Long comment_id) {
        commentHeartService.delete(userId, comment_id);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 10:19 AM
        내용: 댓글 단 글 리스트
    */
    @GetMapping("/comment/mypage")
    public Slice<CommentDTO> searchCommentByUser(@Login Long userId, Pageable pageable) {
        return commentService.searchByUser(userId, pageable);
    }
}
