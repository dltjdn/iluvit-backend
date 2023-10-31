package FIS.iLUVit.domain.centerbookmark.service;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.centerbookmark.exception.CenterBookmarkErrorResult;
import FIS.iLUVit.domain.centerbookmark.exception.CenterBookmarkException;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.centerbookmark.domain.Prefer;
import FIS.iLUVit.domain.review.domain.Review;
import FIS.iLUVit.domain.centerbookmark.dto.CenterBookmarkResponse;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.centerbookmark.repository.CenterBookmarkRepository;
import FIS.iLUVit.domain.review.repository.ReviewRepository;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterBookmarkService {
    private final CenterRepository centerRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final ParentRepository parentRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 즐겨찾는 시설 전체 조회
     */
    public Slice<CenterBookmarkResponse> findCentersByCenterBookmark(Long userId, Pageable pageable) {
        Parent parent = getParent(userId);

        List<Prefer> centerBookmarks = centerBookmarkRepository.findByParent(parent);

        List<CenterBookmarkResponse> centerBookmarkResponses = new ArrayList<>();

        centerBookmarks.forEach((centerBookmark)->{
            List<Review> reviews = reviewRepository.findByCenter(centerBookmark.getCenter());

            // Review 객체들의 score 필드의 평균 계산
            double averageScore = reviews.stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0); // 만약 리뷰가 없는 경우 0.0을 반환

            centerBookmarkResponses.add(CenterBookmarkResponse.of(centerBookmark.getCenter(), averageScore));
        });

        boolean hasNext = false;
        if (centerBookmarks.size() > pageable.getPageSize()) {
            hasNext = true;
            centerBookmarks.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerBookmarkResponses, pageable, hasNext);
    }


    /**
     * 시설 즐겨찾기 등록
     */
    public void saveCenterBookmark(Long userId, Long centerId) {
        Parent parent = getParent(userId);
        Center center = getCenter(centerId);

        centerBookmarkRepository.findByCenterAndParent(center, parent)
                .ifPresent(prefer -> {
                    throw new CenterBookmarkException(CenterBookmarkErrorResult.ALREADY_CENTER_BOOKMARKED);
                });

        Prefer prefer = Prefer.createPrefer(parent, center);
        centerBookmarkRepository.save(prefer);

    }

    /**
     * 시설 즐겨찾기 해제
     */
    public void deleteCenterBookmark(Long userId, Long centerId) {
        Parent parent = getParent(userId);
        Center center = getCenter(centerId);

        Prefer deletedPrefer = centerBookmarkRepository.findByCenterAndParent(center,parent)
                .orElseThrow(() -> new CenterBookmarkException(CenterBookmarkErrorResult.CENTER_BOOKMARK_NOT_FOUND));

        centerBookmarkRepository.delete(deletedPrefer);
    }

    /**
     * 즐겨찾기 한 시설 리스트 삭제
     */
    public void deleteCenterBookmarkByWithdraw(Long userId, Parent parent){
        centerBookmarkRepository.findByParent(parent).forEach(centerBookmark -> {
           deleteCenterBookmark(userId, centerBookmark.getCenter().getId());
        });
    }


    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        return centerRepository.findById(centerId)
                .orElseThrow(()-> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}
