package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import FIS.iLUVit.exception.PreferErrorResult;
import FIS.iLUVit.exception.PreferException;
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
     *   작성내용: 찜한 시설 리스트
     */
    public Slice<CenterPreviewDto> findCentersByPrefer(Long userId, Pageable pageable) {
        return centerRepository.findByPrefer(userId, pageable);
    }


    /**
     *   작성자: 이승범
     *   작성내용: 시설 찜하기
     */
    public Prefer savePrefer(Long userId, Long centerId) {

        centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId)
                .ifPresent(prefer -> {
                    throw new PreferException(PreferErrorResult.ALREADY_PREFER);
                });

        try {
            Parent parent = parentRepository.getById(userId);
            Center center = centerRepository.getById(centerId);
            Prefer prefer = Prefer.createPrefer(parent, center);
            centerBookmarkRepository.saveAndFlush(prefer);
            return prefer;
        } catch (DataIntegrityViolationException e) {
            throw new PreferException(PreferErrorResult.NOT_VALID_CENTER);
        }
    }

    /**
     *   작성자: 이승범
     *   작성내용: 찜한 시설 삭제
     */
    public void deletePrefer(Long userId, Long centerId) {
        Prefer deletedPrefer = centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId)
                .orElseThrow(() -> new PreferException(PreferErrorResult.NOT_VALID_CENTER));

        centerBookmarkRepository.delete(deletedPrefer);
    }

}
