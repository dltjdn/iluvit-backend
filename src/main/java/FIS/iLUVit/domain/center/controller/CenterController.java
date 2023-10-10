package FIS.iLUVit.domain.center.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.center.dto.*;
import FIS.iLUVit.domain.center.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("center")
public class CenterController {

    private final CenterService centerService;

    /**
     * COMMON
     */

    /**
     * 주변 시설 전체 조회
     */
    @PostMapping("search/all")
    public ResponseEntity<List<CenterMapResponse>> getAllCenter(@RequestBody @Validated CenterMapRequest centerMapRequest){
        List<CenterMapResponse> centerByFilterForMap = centerService.findCenterByFilterForMap(centerMapRequest);
        return ResponseEntity.ok(centerByFilterForMap);
    }


    /**
     * 유저가 설정한 필터 기반 시설 조회
     */
    @PostMapping("search")
    public ResponseEntity<Slice<CenterMapFilterResponse>> getCenterByFilter(@Login Long userId, @RequestBody @Validated CenterMapFilterRequest centerMapFilterRequest, Pageable pageable){
        Slice<CenterMapFilterResponse> centerByFilterForMapList = centerService.findCenterByFilterForMapList(userId,centerMapFilterRequest, pageable);
        return ResponseEntity.ok(centerByFilterForMapList);
    }

    /**
     * 시설 상세 조회
     */
    @GetMapping("{centerId}/info")
    public ResponseEntity<CenterFindResponse> getCenterDetails(@PathVariable("centerId") Long centerId){
        CenterFindResponse centerDetailsByCenter = centerService.findCenterDetailsByCenter(centerId);
        return ResponseEntity.ok(centerDetailsByCenter);
    }

    /**
     * 미리보기 배너 용 시설 상세 조회
     */
    @GetMapping("{centerId}/recruit")
    public ResponseEntity<CenterBannerResponse> getCenterDetailsForBanner(@Login Long userId, @PathVariable("centerId") Long centerId){
        CenterBannerResponse centerBannerByCenter = centerService.findCenterBannerByCenter(userId, centerId);
        return ResponseEntity.ok(centerBannerByCenter);
    }


    /**
     * PARENT
     */

    /**
     *  추천 시설 전체 조회 ( 학부모가 선택한 관심 테마를 가지고 있는 시설 조회 )
     */
    @GetMapping("theme")
    public ResponseEntity<List<CenterRecommendResponse>> getAllCenterByTheme(@Login Long userId){
        List<CenterRecommendResponse> recommendCenterWithTheme = centerService.findRecommendCenterWithTheme(userId);
        return ResponseEntity.ok(recommendCenterWithTheme);
    }


    /**
     * TEACHER
     */

    /**
     * 시설 정보 수정
     */
    @PatchMapping("{centerId}")
    public ResponseEntity<Void> updateCenterInfo(@Login Long userId, @PathVariable("centerId") Long centerId,
                                                 @RequestBody @Validated CenterUpdateRequest centerUpdateRequest){

        centerService.modifyCenterInfo(userId, centerId, centerUpdateRequest);

        return ResponseEntity.noContent().build();
    }

    /**
     * 시설 이미지 수정
     */
    @PatchMapping("{centerId}/image")
    public ResponseEntity<Void> updateCenterImage(@Login Long userId, @PathVariable("centerId") Long centerId,
                                                  @Valid @ModelAttribute CenterImageRequest centerImageRequest) {

        centerService.modifyCenterImage(userId, centerId, centerImageRequest);

        return ResponseEntity.noContent().build();
    }

}
