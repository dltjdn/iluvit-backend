package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterPreviewDto;
import FIS.iLUVit.service.CenterBookmarkService;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("center-bookmark")
public class CenterBookmarkController {
    private final CenterBookmarkService centerBookmarkService;

    /**
     * PARENT
     */

    /**
     * 작성자: 이승범
     * 작성내용: 즐겨찾는 시설 전체 조회
     */
    @GetMapping("")
    public Slice<CenterPreviewDto> getAllCenterBookmark(@Login Long userId, Pageable pageable) {
        return centerBookmarkService.findCentersByCenterBookmark(userId, pageable);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설 즐겨찾기 등록
     */
    @PostMapping("{centerId}")
    public void createCenterBookmark(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.saveCenterBookmark(userId, centerId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설 즐겨찾기 해제
     */
    @DeleteMapping("{centerId}")
    public void deleteCenterBookmark(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.deleteCenterBookmark(userId, centerId);
    }

}
