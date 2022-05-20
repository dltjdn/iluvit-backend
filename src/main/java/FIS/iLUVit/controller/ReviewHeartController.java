package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.ReviewHeartRequest;
import FIS.iLUVit.service.ReviewHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewHeartController {

    private final ReviewHeartService reviewHeartService;

    @PostMapping("/reviewHeart/review/{review_id}")
    public void like(@PathVariable("review_id") Long reviewId,
                     @RequestBody ReviewHeartRequest request) {
        reviewHeartService.saveReviewHeart(reviewId, request.getUserId());
    }

    @DeleteMapping("/reviewHeart/review/{review_id}")
    public void cancel(@PathVariable("review_id") Long reviewId,
                     @RequestBody ReviewHeartRequest request) {
        reviewHeartService.deleteReviewHeart(reviewId, request.getUserId());
    }

}
