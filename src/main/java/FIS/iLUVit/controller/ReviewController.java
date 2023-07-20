package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * COMMON
     */

    /**
     * 시설 별 리뷰 전체 조회
     */
    @GetMapping("center/{centerId}")
    public ResponseEntity<Slice<ReviewByCenterDto>> getReviewByCenter(@PathVariable("centerId") Long centerId, Pageable pageable) {
         Slice<ReviewByCenterDto> reviewByCenterDtos = reviewService.findReviewByCenter(centerId, pageable);
         return ResponseEntity.status(HttpStatus.OK).body(reviewByCenterDtos);
    }


    /**
     * PARENT
     */

    /**
     * 학부모가 쓴 리뷰 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<Slice<ReviewByParentDto>> getReviewByParent(@Login Long userId, Pageable pageable) { // @Login 어노테이션 달아야됨.
        Slice<ReviewByParentDto> reviewByParentDtos = reviewService.findReviewListByParent(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(reviewByParentDtos);
    }

    /**
     * 리뷰 등록
     */
    @PostMapping("")
    public ResponseEntity<Void> createReview(@Login Long userId, @RequestBody ReviewDetailDto reviewCreateDTO) {
        reviewService.saveNewReview(userId, reviewCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("{reviewId}")
    public ResponseEntity<Void> updateReview(@Login Long userId, @PathVariable("reviewId") Long reviewId,
                             @RequestBody ReviewDto reviewDto) {
        reviewService.modifyReview(reviewId, userId, reviewDto.getContent());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("{reviewId}")
    public ResponseEntity<Void> deleteReview(@Login Long userId, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


    /**
     * DIRECTOR
     */

    /**
     * 리뷰 답글 등록 및 수정
     */
    @PostMapping("{reviewId}/comment")
    public ResponseEntity<Void> createComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId,
                                @RequestBody ReviewCommentDto reviewCommentDTO) {
        reviewService.saveComment(reviewId, reviewCommentDTO.getComment(), teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 리뷰 답글 삭제
     */
    @DeleteMapping("{reviewId}/comment")
    public ResponseEntity<Void> deleteComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteComment(reviewId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
