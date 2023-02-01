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

    private final ParentService parentService;
    private final CenterBookmarkService centerBookmarkService;

    /**
     * PARENT
     */

    /**
     *   작성날짜: 2022/07/04 2:26 PM
     *   작성자: 이승범
     *   작성내용: 내가 즐겨찾기한 시설
     */
    @GetMapping("")
    public Slice<CenterPreviewDto> findCentersByPrefer(@Login Long userId, Pageable pageable) {
        return centerBookmarkService.findCentersByPrefer(userId, pageable);
    }

    /**
     * 작성날짜: 2022/07/01 5:08 PM
     * 작성자: 이승범
     * 작성내용: 시설 즐겨찾기 등록
     */
    @PostMapping("{centerId}")
    public void savePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.savePrefer(userId, centerId);
    }

    /**
     * 작성날짜: 2022/07/04 2:16 PM
     * 작성자: 이승범
     * 작성내용: 시설 즐겨찾기 해제
     */
    @DeleteMapping("{centerId}")
    public void deletePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        centerBookmarkService.deletePrefer(userId, centerId);
    }

}
