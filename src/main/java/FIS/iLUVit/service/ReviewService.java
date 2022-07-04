package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ReviewByCenterDTO;
import FIS.iLUVit.controller.dto.ReviewByParentDTO;
import FIS.iLUVit.controller.dto.ReviewCreateDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.ReviewException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;

    public ReviewByParentDTO findByParent(Long userId) {
        List<Review> reviews = reviewRepository.findByParent(userId);
        ReviewByParentDTO reviewDtoList = new ReviewByParentDTO();

        reviews.forEach((review) -> {
            reviewDtoList.getReviews().add(new ReviewByParentDTO.ReviewDto(
                    review.getId(), review.getCenter().getName(), review.getContent(), review.getCreateDate()
            ));
        });
        return reviewDtoList;
    }

    public void saveReview(Long userId, ReviewCreateDTO reviewCreateDTO) {

        Parent findUser = parentRepository.getById(userId);
        Center findCenter = centerRepository.findById(reviewCreateDTO.getCenterId())
                .orElseThrow(() -> new CenterException("존재하지 않는 시설"));

        // 유저가 시설에 작성한 리뷰가 이미 존재하는 지 검증
        reviewRepository.findByUserAndCenter(userId, reviewCreateDTO.getCenterId())
                .ifPresent((r) -> {
                    log.info("r.getId : " + r.getId().toString());
                    throw new ReviewException("센터 당 하나의 리뷰만 등록 가능합니다.");
                });

        Review review = Review.createReview(reviewCreateDTO.getContent(), reviewCreateDTO.getScore(),
                reviewCreateDTO.getAnonymous(), findUser, findCenter);
        findCenter.addScore(Score.Review); // 리뷰 작성 시 센터의 스코어 올림
        reviewRepository.save(review);
    }

    public void updateReview(Long reviewId, Long userId, String content) {
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException("존재하지 않는 리뷰 아이디"));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new UserException("수정 권한 없는 유저");
        }
        findReview.updateContent(content);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("존재하지 않는 리뷰"));
        if (!Objects.equals(findReview.getParent().getId(), userId)) {
            throw new UserException("삭제 권한 없는 유저");
        }
        reviewRepository.delete(findReview);
    }

    public ReviewByCenterDTO findByCenter(Long centerId, Long userId, Pageable pageable) {
        // getParent 지연 로딩 쿼리 막음
        Slice<Review> reviews = reviewRepository.findByCenterAndParent(centerId, pageable);

        Slice<ReviewByCenterDTO.ReviewCenterDto> dtos = reviews.map(review -> {
            log.info("userId: " + userId);
            Integer like = review.getReviewHearts().size();
            String imagePath = imageService.getUserProfileDir();
            String encodedProfileImage = imageService.getEncodedProfileImage(imagePath, review.getParent().getId());
            Long teacherId = review.getTeacher() == null ? null : review.getTeacher().getId();
            return new ReviewByCenterDTO.ReviewCenterDto(
                    review.getId(), review.getParent().getId(), review.getParent().getNickName(), review.getContent(), review.getScore(),
                    review.getCreateDate(), review.getCreateTime(), review.getUpdateDate(), review.getUpdateTime(),
                    teacherId, review.getAnswer(), review.getAnswerCreateDate(), review.getAnswerCreateTime(),
                    review.getAnonymous(), like, encodedProfileImage
            );
        });

        return new ReviewByCenterDTO(dtos);
    }

    public void saveComment(Long reviewId, String comment, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("존재하지 않는 리뷰 아이디"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException("존재하지 않는 티처 아이디"));
        log.info("teacher.getCenter() : " + teacher.getCenter().toString());
        log.info("review.getCenter() : " + review.getCenter().toString());
        if (teacher.getAuth() != Auth.DIRECTOR) {
            throw new ReviewException("리뷰 작성은 DIRECTOR 권한만 허용됩니다.");
        }
        if (!teacher.getApproval().equals(Approval.ACCEPT)) {
            throw new UserException("권한이 없는 티처입니다. (승인 대기 혹은 반려 상태)");
        }
        if (teacher.getCenter() != review.getCenter()) {
            throw new UserException("권한없는 티처입니다. (해당 센터의 티처가 아님)");
        }

        review.updateAnswer(comment, teacher);
    }

    public void deleteComment(Long reviewId, Long teacherId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("존재하지 않는 리뷰 아이디"));
        if (!Objects.equals(review.getTeacher().getId(), teacherId)) {
            throw new UserException("삭제 권한 없는 유저");
        }
        review.updateAnswer(null, null); // 대댓글 삭제 -> null
    }
}
