package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import FIS.iLUVit.exception.CenterBookmarkErrorResult;
import FIS.iLUVit.exception.CenterBookmarkException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.CenterBookmarkRepository;
import FIS.iLUVit.dto.center.CenterPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterBookmarkService {
    private final CenterRepository centerRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final ParentRepository parentRepository;

    /**
     *   작성자: 이승범
     *   작성내용: 유저가 즐겨찾기한 시설을 조회합니다
     */
    public Slice<CenterPreviewDto> findCentersByCenterBookmark(Long userId, Pageable pageable) {
        return centerRepository.findByPrefer(userId, pageable);
    }


    /**
     *   작성자: 이승범
     *   작성내용: 해당 시설을 시설 즐겨찾기에 등록합니다
     */
    public Prefer saveCenterBookmark(Long userId, Long centerId) {

        centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId)
                .ifPresent(prefer -> {
                    throw new CenterBookmarkException(CenterBookmarkErrorResult.ALREADY_PREFER);
                });

        try {
            Parent parent = parentRepository.getById(userId);
            Center center = centerRepository.getById(centerId);
            Prefer prefer = Prefer.createPrefer(parent, center);
            centerBookmarkRepository.saveAndFlush(prefer);
            return prefer;
        } catch (DataIntegrityViolationException e) {
            throw new CenterBookmarkException(CenterBookmarkErrorResult.NOT_VALID_CENTER);
        }
    }

    /**
     *   작성자: 이승범
     *   작성내용: 해당 시설의 시설 즐겨찾기를 해제합니다
     */
    public void deleteCenterBookmark(Long userId, Long centerId) {
        Prefer deletedPrefer = centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId)
                .orElseThrow(() -> new CenterBookmarkException(CenterBookmarkErrorResult.NOT_VALID_CENTER));

        centerBookmarkRepository.delete(deletedPrefer);
    }

}
