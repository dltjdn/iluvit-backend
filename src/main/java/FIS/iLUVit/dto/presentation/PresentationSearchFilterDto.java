package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.common.embeddable.Area;
import FIS.iLUVit.domain.common.embeddable.Theme;
import FIS.iLUVit.domain.iluvit.enumtype.KindOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresentationSearchFilterDto {
    private List<Area> areas = new ArrayList<>();
    private Theme theme;
    private Integer interestedAge;
    private String searchContent;
    private KindOf kindOf;
}
