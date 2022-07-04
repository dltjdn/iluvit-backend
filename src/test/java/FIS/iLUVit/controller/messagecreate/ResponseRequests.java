package FIS.iLUVit.controller.messagecreate;

import FIS.iLUVit.controller.dto.CenterSearchFilterDTO;
import FIS.iLUVit.controller.dto.CenterSearchMapFilterDTO;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;

import java.util.Arrays;
import java.util.List;

public class ResponseRequests {

    public CenterSearchFilterDTO centerSearchFilterRequest(List<Area> areas){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterSearchFilterDTO.builder()
                .areas(areas)
                .interestedAge(4)
                .kindOf(KindOf.Kindergarten)
                .theme(theme)
                .build();
    }

    public List<CenterPreview> centerPreviewResponse(){

        Area area = new Area("sido", "sigungu");

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);
        return Arrays.asList(CenterPreview.builder()
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

    public CenterSearchMapFilterDTO centerSearchMapFilterDTO(double longitude, double latitude){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterSearchMapFilterDTO.builder()
                .longitude(longitude)
                .latitude(latitude)
                .theme(theme)
                .interestedAge(5)
                .kindOf(KindOf.ALL)
                .distance(5)        //5km
                .build();
    }

    public CenterAndDistancePreview centerAndDistancePreview(){

        Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);

        return CenterAndDistancePreview.builder()
                .id(3L)
                .name("센터정보")
                .owner("현승구")
                .estType("공립")
                .tel("test")
                .startTime("9시 00분")
                .endTime("16시 00분")
                .minAge(4)
                .maxAge(5)
                .address("가리봉동")
                .area(new Area("서울특별시", "구로구"))
                .longitude(32.1561)
                .latitude(127.2312)
                .theme(theme)
                .build();
    }

}
