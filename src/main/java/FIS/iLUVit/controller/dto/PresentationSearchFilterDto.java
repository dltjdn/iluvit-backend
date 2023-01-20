package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationSearchFilterDto {
    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private String searchContent;
    private KindOf kindOf;
}
