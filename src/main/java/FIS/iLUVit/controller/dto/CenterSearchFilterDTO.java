package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CenterSearchFilterDTO {

    @Size(min = 1, max = 3, message = "최소 1개 이상의 지역을 선택해야합니다")
    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private KindOf kindOf;                  // 시설 종류

    @Builder
    public CenterSearchFilterDTO(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf) {
        this.areas = areas;
        this.theme = theme;
        this.interestedAge = interestedAge;
        this.kindOf = kindOf;
    }

}
