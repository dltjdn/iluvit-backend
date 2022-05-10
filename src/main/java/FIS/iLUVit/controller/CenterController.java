package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.CenterInfoResponseDto;
import FIS.iLUVit.controller.dto.CenterSearchDto;
import FIS.iLUVit.controller.dto.CenterSearchFilterDTO;
import FIS.iLUVit.controller.dto.CenterSearchMapFilterDTO;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CenterController {

    private final CenterService centerService;

    /**
     * center 검색 정보 반환 front 검색인자 값 - 시도, 시군구 값(list) 그리고 offset 과 갯수 몇개 가져올건지 <P>
     * 반환값으로 center Preview 정보와 startIndex와 endIndex 보내준다.
     */
    @PostMapping("/center/search")
    public CenterSearchDto<CenterPreview> searchByFilter(@RequestBody CenterSearchFilterDTO dto,
                                                         @RequestParam(required = false) Integer offset,
                                                         @RequestParam(required = false) Integer limit){
        List<CenterPreview> centerPreviews = centerService.findByFilter(dto.getAreas(), dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), offset, limit);
        return new CenterSearchDto<CenterPreview>(centerPreviews, offset, offset+centerPreviews.size());
    }

    /**
     * center 검색 정보 반환 front 검색인자 값 - 위도 경도 지도와 관련하여 api 던져준다
     */
    @PostMapping("/center/map/search")
    public List<CenterAndDistancePreview> searchByFilterAndMap(@RequestBody CenterSearchMapFilterDTO dto){
        List<CenterAndDistancePreview> center = centerService.findByFilterAndMap(dto.getLongitude(), dto.getLatitude() ,dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), dto.getDistance());
        return center;
    }

    /**
     * Id 기반 center 정보값 반환하기 기본정보 + 프로그램 + 기본시설 + 부가시설 반환
     */
    @GetMapping("/center/{center_id}/Info")
    public CenterInfoResponseDto centerInfo(@PathVariable("center_id") Long id){
        return new CenterInfoResponseDto(centerService.findInfoById(id));
    }
}
