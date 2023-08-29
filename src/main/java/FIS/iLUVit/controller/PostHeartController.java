package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.PostHeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글 좋아요 API")
@RequestMapping("post-heart")
public class PostHeartController {

    private final PostHeartService postHeartService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 등록
    */
    @Operation(summary = "게시글 좋아요 등록", description = "게시글의 좋아요 버튼을 클릭합니다.")
    @PostMapping("{postId}")
    public Long createPostHeart(@Login Long userId, @PathVariable("postId") Long postId) {
        return postHeartService.savePostHeart(userId, postId);
    }
    
    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 좋아요 취소
     * 비고: 기존에 좋아요 눌렀던 상태여야 취소 가능
    */
    @Operation(summary = "게시글 좋아요 취소", description = "클릭되어 있는 게시글의 좋아요 버튼을 눌러 좋아요를 취소합니다.")
    @DeleteMapping("{postId}")
    public void deletePostHeart(@Login Long userId, @PathVariable("postId") Long postId){
        postHeartService.deletePostHeart(userId,postId);
    }

}