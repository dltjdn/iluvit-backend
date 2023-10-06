package FIS.iLUVit.domain.presentation.dto;

import FIS.iLUVit.domain.center.domain.Area;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.center.domain.KindOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresentationSearchFilterRequest {
    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private String searchContent;
    private KindOf kindOf;
}
