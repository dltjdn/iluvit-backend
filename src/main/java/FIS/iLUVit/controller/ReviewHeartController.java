package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ReviewHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewHeartController {

    private final ReviewHeartService reviewHeartService;

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 2:21 PM
        내용: 리뷰 좋아요
    */
    @PostMapping("/user/reviewHeart/review/{review_id}")
    public Long like(@PathVariable("review_id") Long reviewId,
                     @Login Long userId) {
        return reviewHeartService.saveReviewHeart(reviewId, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 2:23 PM
        내용: 리뷰 좋아요 취소
    */
    @DeleteMapping("/user/reviewHeart/review/{review_id}")
    public void cancel(@PathVariable("review_id") Long reviewId,
                       @Login Long userId) {
        reviewHeartService.deleteReviewHeart(reviewId, userId);
    }

}
