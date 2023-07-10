package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
     * 작성자: 이창윤
     * 작성내용: 시설 별 리뷰 전체 조회
     */
    @GetMapping("center/{centerId}")
    public Slice<ReviewByCenterDto> getReviewByCenter(@PathVariable(name = "centerId") Long centerId,
                                                   Pageable pageable) {
        return reviewService.findReviewByCenter(centerId, pageable);
    }


    /**
     * PARENT
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 학부모가 쓴 리뷰 전체 조회
     */
    @GetMapping("")
    public Slice<ReviewByParentDto> getReviewByParent(@Login Long userId, Pageable pageable) { // @Login 어노테이션 달아야됨.
        return reviewService.findReviewListByParent(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 등록
    */
    @PostMapping("")
    public Long createReview(@Login Long userId, @RequestBody ReviewDetailDto reviewCreateDTO) {
        return reviewService.saveNewReview(userId, reviewCreateDTO);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 수정
    */
    @PatchMapping("{reviewId}")
    public void updateReview(@Login Long userId, @PathVariable(name = "reviewId") Long reviewId,
                             @RequestBody ReviewDto reviewDto) {
        reviewService.modifyReview(reviewId, userId, reviewDto.getContent());
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 삭제
    */
    @DeleteMapping("{reviewId}")
    public void deleteReview(@Login Long userId, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 답글 등록 및 수정
     * 비고: 답글 달 수 있는 권한은 Director 만 가능
     */
    @PostMapping("{reviewId}/comment")
    public Long createComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId,
                                @RequestBody ReviewCommentDto reviewCommentDTO) {
        return reviewService.saveComment(reviewId, reviewCommentDTO.getComment(), teacherId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 답글 삭제
    */
    @DeleteMapping("{reviewId}/comment")
    public void deleteComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteComment(reviewId, teacherId);
    }

}
