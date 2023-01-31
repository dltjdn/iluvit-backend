package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.comment.CommentDto;
import FIS.iLUVit.dto.comment.CommentRequest;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * COMMON
     */

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 10:09 AM
     내용: comment_id 값이 null일 경우 댓글 작성, comment_id 값까지 보내는 경우 대댓글 작성
    */
    @PostMapping("")
    public Long registerComment(@Login Long userId,
                                @RequestParam("post_id") Long postId,
                                @RequestParam(value = "comment_id", required = false) Long commentId,
                                @RequestBody CommentRequest request) {

        return commentService.registerComment(userId, postId, commentId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 10:14 AM
     내용: 댓글 삭제, 댓글 데이터 지우지 않고 내용(content), 작성자(user)만 null로 변경
    */
    @PatchMapping("")
    public Long deleteComment(@Login Long userId,
                              @RequestParam("comment_id") Long commentId) {
        return commentService.deleteComment(userId, commentId);
        // 삭제하면 그 대댓글까지 삭제? or 대댓글은 남김?
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 10:19 AM
     내용: 댓글 단 글 목록
     */
    @GetMapping("mypage")
    public Slice<CommentDto> searchCommentByUser(@Login Long userId, Pageable pageable) {
        return commentService.searchByUser(userId, pageable);
    }

}
