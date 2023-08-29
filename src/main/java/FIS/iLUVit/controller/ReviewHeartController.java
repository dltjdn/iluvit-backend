package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ReviewHeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "리뷰 좋아요 API")
@RequestMapping("review-heart")
public class ReviewHeartController {

    private final ReviewHeartService reviewHeartService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 내용: 리뷰 좋아요 등록
    */
    @Operation(summary = "리뷰 좋아요 등록", description = "해당 리뷰에 좋아요를 등록합니다.")
    @PostMapping("{reviewId}")
    public Long createReviewHeart(@PathVariable("reviewId") Long reviewId,
                     @Login Long userId) {
        return reviewHeartService.saveReviewHeart(reviewId, userId);
    }

    /**
        작성자: 이창윤
        내용: 리뷰 좋아요 취소
    */
    @Operation(summary = "리뷰 좋아요 취소", description = "해당 리뷰에 등록한 좋아요를 취소합니다.")
    @DeleteMapping("{reviewId}")
    public void deleteReviewHeart(@PathVariable("reviewId") Long reviewId,
                       @Login Long userId) {
        reviewHeartService.deleteReviewHeart(reviewId, userId);
    }

}
