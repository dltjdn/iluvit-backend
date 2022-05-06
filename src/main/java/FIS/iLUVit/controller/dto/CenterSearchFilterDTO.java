package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CenterSearchFilterDTO {

    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private String kindOf;                  // 시설 종류

}
