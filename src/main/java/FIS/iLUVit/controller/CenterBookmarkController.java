package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterPreviewDto;
import FIS.iLUVit.service.CenterBookmarkService;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 즐겨찾는 시설 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<Slice<CenterPreviewDto>> getAllCenterBookmark(@Login Long userId, Pageable pageable) {
        Slice<CenterPreviewDto> centerPreviewDtos = centerBookmarkService.findCentersByCenterBookmark(userId, pageable);
        return ResponseEntity.ok(centerPreviewDtos);
    }

    /**
     * 시설 즐겨찾기 등록
     */
    @PostMapping("{centerId}")
    public ResponseEntity<Void> createCenterBookmark(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.saveCenterBookmark(userId, centerId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 시설 즐겨찾기 해제
     */
    @DeleteMapping("{centerId}")
    public ResponseEntity<Void> deleteCenterBookmark(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.deleteCenterBookmark(userId, centerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
