package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.comment.CommentPostDto;
import FIS.iLUVit.dto.comment.CommentRequestDto;
import FIS.iLUVit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 댓글 작성 (comment_id 값이 null일 경우 댓글 작성, comment_id 값까지 보내는 경우 대댓글 작성)
    */
    @PostMapping(value={"{postId}","{postId}/{commentId}"})
    public ResponseEntity<Void> createComment(@Login Long userId, @PathVariable("postId") Long postId,
                                @PathVariable(required = false, value="commentId") Long commentId, @RequestBody CommentRequestDto commentRequest) {
        commentService.saveNewComment(userId, postId, commentId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 댓글 삭제 ( 댓글 데이터 지우지 않고 내용, 작성자만 null로 변경)
    */
    @PatchMapping("{commentId}")
    public ResponseEntity<Void> deleteComment(@Login Long userId, @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 댓글 단 글 전체 조회
     */
    @GetMapping("mypage")
    public ResponseEntity<Slice<CommentPostDto>> getCommentByUser(@Login Long userId, Pageable pageable) {
        Slice<CommentPostDto> commentResponseDtos = commentService.findCommentByUser(userId, pageable);
        return ResponseEntity.ok(commentResponseDtos);
    }

}
