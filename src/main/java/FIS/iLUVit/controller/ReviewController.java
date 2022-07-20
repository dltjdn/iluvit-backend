package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:42 PM
        내용: 학부모가 작성한 리뷰 조회
    */
    @GetMapping("/review")
    public ReviewByParentDTO searchByParent(@Login Long userId) { // @Login 어노테이션 달아야됨.
        return reviewService.findByParent(userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:49 PM
        내용: 학부모가 쓴 리뷰 등록
    */
    @PostMapping("/review")
    public Long registerReview(@Login Long userId, @RequestBody ReviewCreateDTO reviewCreateDTO) {
        return reviewService.saveReview(userId, reviewCreateDTO);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:49 PM
        내용: 리뷰 수정
    */
    @PatchMapping("/review/{review_id}")
    public void updateReview(@Login Long userId, @PathVariable(name = "review_id") Long reviewId,
                             @RequestBody ReviewUpdateDTO reviewUpdateDto) {
        reviewService.updateReview(reviewId, userId, reviewUpdateDto.getContent());
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:49 PM
        내용: 리뷰 삭제
    */
    @DeleteMapping("/review/{review_id}")
    public void deleteReview(@Login Long userId, @PathVariable("review_id") Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:49 PM
        내용: 센터에 올라온 리뷰들 조회
    */
    @GetMapping("/review/center/{center_id}")
    public ReviewByCenterDTO searchByCenter(@PathVariable(name = "center_id") Long centerId,
                                            Pageable pageable) {
        return reviewService.findByCenter(centerId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:50 PM
        내용: 시설에 달린 리뷰 답글 등록 + 수정, 답글 달 수 있는 권한은 Director 만 가능
     */
    @PostMapping("/review/{review_id}/comment")
    public void registerComment(@Login Long teacherId, @PathVariable("review_id") Long reviewId,
                                @RequestBody ReviewCommentDTO reviewCommentDTO) {
        reviewService.saveComment(reviewId, reviewCommentDTO.getComment(), teacherId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:52 PM
        내용: 시설에 달린 리뷰 답글 삭제
    */
    @DeleteMapping("/review/{review_id}/comment")
    public void deleteComment(@Login Long teacherId, @PathVariable("review_id") Long reviewId) {
        reviewService.deleteComment(reviewId, teacherId);
    }
}
