package FIS.iLUVit.controller.messagecreate;

import FIS.iLUVit.controller.dto.CenterSearchFilterDto;
import FIS.iLUVit.controller.dto.CenterSearchMapFilterDto;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreviewDto;
import FIS.iLUVit.repository.dto.CenterPreviewDto;

import java.util.Arrays;
import java.util.List;

public class ResponseRequests {

    public CenterSearchFilterDto centerSearchFilterRequest(List<Area> areas){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterSearchFilterDto.builder()
                .areas(areas)
                .interestedAge(4)
                .kindOf(KindOf.Kindergarten)
                .theme(theme)
                .build();
    }

    public List<CenterPreviewDto> centerPreviewResponse(){

        Area area = new Area("sido", "sigungu");

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);
        return Arrays.asList(CenterPreviewDto.builder()
                .id(1L)
                .name("test")
                .owner("test")
                .director("test")
                .estType("test")
                .tel("test")
                .startTime("16시 30분")
                .endTime("9시 00분")
                .minAge(6)
                .maxAge(10)
                .address("test")
                .area(area)
                .longitude(10.0)
                .latitude(10.0)
                .theme(theme)
                .profileImage("image")
                .starAverage(5.0)
                .build());
    }

    public CenterSearchMapFilterDto centerSearchMapFilterDTO(double longitude, double latitude){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterSearchMapFilterDto.builder()
                .longitude(longitude)
                .latitude(latitude)
                .kindOf(KindOf.ALL)
                .distance(5)        //5km
                .build();
    }

    public CenterAndDistancePreviewDto centerAndDistancePreview(){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterAndDistancePreviewDto.builder()
                .id(3L)
                .name("센터정보")
                .estType("공립")
                .tel("test")
                .startTime("9시 00분")
                .endTime("16시 00분")
                .minAge(4)
                .maxAge(5)
                .address("가리봉동")
                .longitude(32.1561)
                .latitude(127.2312)
                .theme(theme)
                .build();
    }

}
