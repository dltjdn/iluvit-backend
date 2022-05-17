package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CenterSearchFilterDTO {

    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private String kindOf;                  // 시설 종류

    public CenterSearchFilterDTO(List<Area> areas, Theme theme, Integer interestedAge, String kindOf) {
        this.areas = areas;
        this.theme = theme;
        this.interestedAge = interestedAge;
        this.kindOf = kindOf;
    }
}
