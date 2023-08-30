package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.CommentHeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "댓글 좋아요 API")
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
    @Operation(summary = "댓글 좋아요 등록", description = "글")
    @PostMapping("{commentId}")
    public Long createCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.saveCommentHeart(userId, commentId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 댓글 좋아요 취소
     */
    @Operation(summary = "댓글 좋아요 취소", description = "클릭되어 있는 댓글의 좋아요 버튼을 눌러 좋아요를 취소합니다.")
    @DeleteMapping("{commentId}")
    public Long deleteCommentHeart(@Login Long userId, @PathVariable Long commentId) {
        return commentHeartService.deleteCommentHeart(userId, commentId);
    }

}
