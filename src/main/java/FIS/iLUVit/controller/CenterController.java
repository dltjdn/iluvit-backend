package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.CenterSearchFilterDTO;
import FIS.iLUVit.controller.dto.CenterSearchMapFilterDTO;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.repository.CenterAndDistance;
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

    @PostMapping("/center/search")
    public void searchByFilter(@RequestBody CenterSearchFilterDTO dto,
                                     @RequestParam(required = false) Integer offset,
                                     @RequestParam(required = false) Integer limit){
        List<Center> center = centerService.findByFilter(dto.getAreas(), dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), offset, limit);
    }

    @PostMapping("/center/map/search")
    public List<CenterAndDistance> searchByFilterAndMap(@RequestBody CenterSearchMapFilterDTO dto){
        List<CenterAndDistance> center = centerService.findByFilterAndMap(dto.getLongitude(), dto.getLatitude() ,dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), dto.getDistance());
        return center;
    }

}
