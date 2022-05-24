package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 학부모가 작성한 리뷰 조회
    @GetMapping("/review")
    public ReviewByParentDTO searchByParent(@Login Long userId) { // @Login 어노테이션 달아야됨.
        return reviewService.findByParent(userId);
    }

    // 학부모가 작성한 리뷰 등록
    @PostMapping("/review")
    public void registerReview(@Login Long userId, @RequestBody ReviewCreateDTO reviewCreateDTO) {
        reviewService.saveReview(userId, reviewCreateDTO);
    }

    // 학부모가 작성한 리뷰 수정
    @PatchMapping("/review/{review_id}")
    public void updateReview(@Login Long userId, @PathVariable(name = "review_id") Long reviewId,
                             @RequestBody ReviewUpdateDTO reviewUpdateDto) {
        reviewService.updateReview(reviewId, userId, reviewUpdateDto.getContent());
    }

    // 학부모가 작성한 리뷰 삭제
    @DeleteMapping("/review/{review_id}")
    public void deleteReview(@Login Long userId, @PathVariable("review_id") Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
    }

    // 센터에 달린 리뷰 조회
    @GetMapping("/review/center/{center_id}")
    public ReviewByCenterDTO searchByCenter(@Login Long userId, @PathVariable(name = "center_id") Long centerId) {
        return reviewService.findByCenter(centerId, userId);
    }

    // 선생님이 단 답글 등록 (수정은 X)
    @PostMapping("/review/{review_id}/comment")
    public void registerComment(@Login Long teacherId, @PathVariable("review_id") Long reviewId,
                                @RequestBody ReviewCommentDTO reviewCommentDTO) {
        reviewService.saveComment(reviewId, reviewCommentDTO.getComment(), teacherId);
    }

    // 선생님이 단 답글 삭제
    @DeleteMapping("/review/{review_id}/comment")
    public void deleteComment(@Login Long teacherId, @PathVariable("review_id") Long reviewId) {
        reviewService.deleteComment(reviewId, teacherId);
    }
}
