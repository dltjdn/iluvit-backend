package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "리뷰 API")
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
    @Operation(summary = "시설 별 리뷰 전체 조회", description = "해당 시설에 대한 리뷰 목록을 조회합니다.")
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
    @Operation(summary = "학부모가 쓴 리뷰 전체 조회", description = "내가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("")
    public Slice<ReviewByParentDto> getReviewByParent(@Login Long userId, Pageable pageable) { // @Login 어노테이션 달아야됨.
        return reviewService.findReviewListByParent(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 등록
    */
    @Operation(summary = "리뷰 등록", description = "아이가 등록되어 승인 완료된 시설에 학부모가 리뷰를 작성합니다.")
    @PostMapping("")
    public Long createReview(@Login Long userId, @RequestBody ReviewDetailDto reviewCreateDTO) {
        return reviewService.saveNewReview(userId, reviewCreateDTO);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 수정
    */
    @Operation(summary = "리뷰 수정", description = "내가 작성한 리뷰를 수정합니다.")
    @PatchMapping("{reviewId}")
    public void updateReview(@Login Long userId, @PathVariable(name = "reviewId") Long reviewId,
                             @RequestBody ReviewDto reviewDto) {
        reviewService.modifyReview(reviewId, userId, reviewDto.getContent());
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 삭제
    */
    @Operation(summary = "리뷰 삭제", description = "내가 작성한 리뷰를 삭제합니다.")
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
    @Operation(summary = "리뷰 답글 등록 및 수정", description = "시설에 등록된 교사가 시설에 등록된 리뷰에 대한 답글을 작성합니다.")
    @PostMapping("{reviewId}/comment")
    public Long createComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId,
                                @RequestBody ReviewCommentDto reviewCommentDTO) {
        return reviewService.saveComment(reviewId, reviewCommentDTO.getComment(), teacherId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 리뷰 답글 삭제
    */
    @Operation(summary = "리뷰 답글 삭제", description = "내가 작성한 리뷰의 답글을 삭제합니다.")
    @DeleteMapping("{reviewId}/comment")
    public void deleteComment(@Login Long teacherId, @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteComment(reviewId, teacherId);
    }

}
