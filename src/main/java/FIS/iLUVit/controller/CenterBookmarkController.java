package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.CenterService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("center-bookmark")
public class CenterBookmarkController {

    private final ParentService parentService;
    private final CenterService centerService;

    /**
     *   작성날짜: 2022/07/04 2:26 PM
     *   작성자: 이승범
     *   작성내용: 찜한 시설 리스트
     */
    @GetMapping("list")
    public Slice<CenterPreview> findCentersByPrefer(@Login Long userId, Pageable pageable) {
        return centerService.findCentersByPrefer(userId, pageable);
    }

    /**
     * 작성날짜: 2022/07/01 5:08 PM
     * 작성자: 이승범
     * 작성내용: 시설 찜하기
     */
    @PostMapping("{centerId}")
    public void savePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        parentService.savePrefer(userId, centerId);
    }

    /**
     * 작성날짜: 2022/07/04 2:16 PM
     * 작성자: 이승범
     * 작성내용: 시설 찜 해제
     */
    @DeleteMapping("{centerId}")
    public void deletePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        parentService.deletePrefer(userId, centerId);
    }

}
