package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 학부모가 작성한 리뷰 반환
    @GetMapping("/review")
    public ReviewByParentDTO searchByParent(Long userId) { // @Login 어노테이션 달아야됨.
        return reviewService.findByParent(userId);
    }

    // 학부모가 작성한 리뷰 등록
    @PostMapping("/review")
    public void registerReview(Long userId, Long centerId, ReviewCreateDTO reviewCreateDTO) {
        reviewService.saveReview(userId, centerId, reviewCreateDTO);
    }

    @PatchMapping("/review/{review_id}")
    public void updateReview(@PathVariable(name = "review_id") Long reviewId,
                             ReviewUpdateDTO reviewUpdateDto) {
        reviewService.updateReview(reviewId, reviewUpdateDto.getContent());
    }

    @GetMapping("/review/center/{center_id}")
    public ReviewByCenterDTO searchByCenter(@PathVariable(name = "center_id") Long centerId) {
        return reviewService.findByCenter(centerId);
    }

    @PostMapping("/review/{review_id}/comment")
    public void registerComment(@PathVariable("review_id") Long reviewId,
                                ReviewCommentDTO reviewCommentDTO) {
        reviewService.saveComment(reviewId, reviewCommentDTO.getComment());
    }

//    @PatchMapping("/review/{review_id}/comment")
//    public void updateComment(@PathVariable("review_id") Long reviewId,
//                              ReviewCommentDTO reviewCommentDTO) {
//        reviewService.saveComment(reviewId, reviewCommentDTO.getComment());
//    }
//    대댓글 하나 인데 등록이랑 업데이트가 따로 있어야 하는지 ??

    @DeleteMapping("/review/{review_id}")
    public void deleteReview(@PathVariable("review_id") Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @DeleteMapping("/review/{review_id}/comment")
    public void deleteComment(@PathVariable("review_id") Long reviewId) {
        reviewService.deleteComment(reviewId);
    }
}
