package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ReviewByCenterDTO;
import FIS.iLUVit.controller.dto.ReviewByParentDTO;
import FIS.iLUVit.controller.dto.ReviewCreateDTO;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.repository.ReviewRepository;
import FIS.iLUVit.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TeacherRepository teacherRepository;

    public ReviewByParentDTO findByParent(Long userId) {
        Parent findUser = new Parent(); // userRepository.findById(userId); 학부모 정보 find
        List<Review> reviews = reviewRepository.findByParent(findUser);
        ReviewByParentDTO reviewDtoList = new ReviewByParentDTO();

        reviews.forEach((review) -> {
            reviewDtoList.getReviews().add(new ReviewByParentDTO.ReviewDto(
                    review.getCenter().getName(), review.getContent(), review.getCreateDate()
            ));
        });

        return reviewDtoList;
    }

    public void saveReview(Long userId, Long centerId, ReviewCreateDTO reviewCreateDTO) {

        Parent findUser = new Parent(); // userRepository.findById(userId); 학부모 정보 find
        Center findCenter = new Center(); // centerRepository.findByName(centerId); 센터 정보 find
        Review review = Review.createReview(reviewCreateDTO.getContent(), reviewCreateDTO.getScore(),
                reviewCreateDTO.getAnonymous(), findUser, findCenter);
        reviewRepository.save(review);
    }

    public void updateReview(Long reviewId, String content) {
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new RuntimeException("존재하지 않는 리뷰 아이디"));
        findReview.updateContent(content);
    }

    public ReviewByCenterDTO findByCenter(Long centerId) {
        List<Review> reviews = reviewRepository.findByCenter(centerId);
        ReviewByCenterDTO reviewByCenterDTO = new ReviewByCenterDTO();
        reviews.forEach((review) -> {
            reviewByCenterDTO.getReviews().add(new ReviewByCenterDTO.ReviewCenterDto(
               review.getId(), review.getParent().getNickName(), review.getContent(), review.getScore(),
                    review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(),
                    review.getUpdateTime(), review.getAnswer(), review.getAnonymous()
            ));
        });
        return reviewByCenterDTO;
    }

    public void saveComment(Long reviewId, String comment, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 리뷰 아이디"));
        log.info(teacherId.toString());
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 티처 아이디"));
        if (!teacher.getApproval().equals(Approval.ACCEPT)) {
            throw new IllegalStateException("권한이 없는 티처입니다. (승인 대기 혹은 반려 상태)");
        }
        log.info("teacher.getCenter() : " + teacher.getCenter().toString());
        log.info("review.getCenter() : " + review.getCenter().toString());
        if (teacher.getCenter() != review.getCenter()) {
            throw new IllegalStateException("권한없는 티처입니다. (해당 센터의 티처가 아님)");
        }

        review.updateAnswer(comment, teacher);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public void deleteComment(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 리뷰 아이디"));
        review.updateAnswer(null, null); // 대댓글 삭제 -> null
    }
}
