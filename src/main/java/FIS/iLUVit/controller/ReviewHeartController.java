package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ReviewHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("review-heart")
public class ReviewHeartController {

    private final ReviewHeartService reviewHeartService;

    /**
     * COMMON
     */

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 2:21 PM
        내용: 리뷰 좋아요
    */
    @PostMapping("{reviewId}")
    public Long like(@PathVariable("reviewId") Long reviewId,
                     @Login Long userId) {
        return reviewHeartService.saveReviewHeart(reviewId, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 2:23 PM
        내용: 리뷰 좋아요 취소
    */
    @DeleteMapping("{reviewId}")
    public void cancel(@PathVariable("reviewId") Long reviewId,
                       @Login Long userId) {
        reviewHeartService.deleteReviewHeart(reviewId, userId);
    }

}
