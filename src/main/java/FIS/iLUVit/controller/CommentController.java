package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.comment.CommentDto;
import FIS.iLUVit.dto.comment.CommentRequest;
import FIS.iLUVit.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "댓글 API")
@RequestMapping("comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 댓글 작성
     * 비고: comment_id 값이 null일 경우 댓글 작성, comment_id 값까지 보내는 경우 대댓글 작성
    */
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    @PostMapping(value={"{postId}","{postId}/{commentId}"})
    public Long createComment(@Login Long userId,
                                @PathVariable("postId") Long postId,
                                @PathVariable(required = false, value="commentId") Long commentId,
                                @RequestBody CommentRequest request) {

        return commentService.saveNewComment(userId, postId, commentId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 댓글 삭제
     * 비고: 댓글 데이터 지우지 않고 내용(content), 작성자(user)만 null로 변경
     * 삭제하면 그 대댓글까지 삭제? or 대댓글은 남김?
    */
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (댓글 데이터 지우지 않고 내용, 작성자만 null로 변경)")
    @PatchMapping("{commentId}")
    public Long deleteComment(@Login Long userId,
                              @PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(userId, commentId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 댓글 단 글 전체 조회
     */
    @Operation(summary = "댓글 단 글 전체 조회", description = "댓글 단 글 목록을 조회합니다.")
    @GetMapping("mypage")
    public Slice<CommentDto> getCommentByUser(@Login Long userId, Pageable pageable) {
        return commentService.findCommentByUser(userId, pageable);

    }

}
