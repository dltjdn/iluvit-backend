package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.dto.center.CenterBookmarkResponse;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.CenterBookmarkRepository;
import FIS.iLUVit.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Prefer> centerBookmarks = centerBookmarkRepository.findByParent(parent);

        List<CenterBookmarkResponse> centerBookmarkResponses = new ArrayList<>();

        centerBookmarks.forEach((centerBookmark)->{
            List<Review> reviews = reviewRepository.findByCenter(centerBookmark.getCenter());

            // Review 객체들의 score 필드의 평균 계산
            double averageScore = reviews.stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0); // 만약 리뷰가 없는 경우 0.0을 반환

            centerBookmarkResponses.add(new CenterBookmarkResponse(centerBookmark.getCenter(), averageScore));
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
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Center center = centerRepository.findById(centerId)
                        .orElseThrow(()-> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        centerBookmarkRepository.findByCenterAndParent(center, parent)
                .ifPresent(prefer -> {
                    throw new CenterBookmarkException(CenterBookmarkErrorResult.ALREADY_PREFER);
                });

        try {
            Prefer prefer = Prefer.createPrefer(parent, center);
            centerBookmarkRepository.saveAndFlush(prefer);
        } catch (DataIntegrityViolationException e) {
            throw new CenterBookmarkException(CenterBookmarkErrorResult.NOT_VALID_CENTER);
        }
    }

    /**
     * 시설 즐겨찾기 해제
     */
    public void deleteCenterBookmark(Long userId, Long centerId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Center center = centerRepository.findById(centerId)
                .orElseThrow(()-> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        Prefer deletedPrefer = centerBookmarkRepository.findByCenterAndParent(center,parent)
                .orElseThrow(() -> new CenterBookmarkException(CenterBookmarkErrorResult.NOT_VALID_CENTER));

        centerBookmarkRepository.delete(deletedPrefer);
    }

}
