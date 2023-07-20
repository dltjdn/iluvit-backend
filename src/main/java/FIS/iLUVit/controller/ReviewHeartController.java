package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ReviewHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 리뷰 좋아요 등록
     */
    @PostMapping("{reviewId}")
    public ResponseEntity<Void> createReviewHeart(@PathVariable("reviewId") Long reviewId,
                                            @Login Long userId) {
        reviewHeartService.saveReviewHeart(reviewId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 리뷰 좋아요 취소
     */
    @DeleteMapping("{reviewId}")
    public ResponseEntity<Void> deleteReviewHeart(@PathVariable("reviewId") Long reviewId,
                       @Login Long userId) {
        reviewHeartService.deleteReviewHeart(reviewId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
