package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ReviewHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewHeartController {

    private final ReviewHeartService reviewHeartService;

    @PostMapping("/reviewHeart/review/{review_id}")
    public void like(@PathVariable("review_id") Long reviewId,
                     @Login Long userId) {
        reviewHeartService.saveReviewHeart(reviewId, userId);
    }

    @DeleteMapping("/reviewHeart/review/{review_id}")
    public void cancel(@PathVariable("review_id") Long reviewId,
                       @Login Long userId) {
        reviewHeartService.deleteReviewHeart(reviewId, userId);
    }

}
