package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.iluvit.enumtype.KindOf;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CenterSearchMapFilterDto {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private KindOf kindOf;                  // 시설종류

    private List<Long> centerIds = new ArrayList<>();

    @Builder
    public CenterSearchMapFilterDto(double longitude, double latitude, KindOf kindOf, Integer distance, List<Long> idList) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.kindOf = kindOf;
        this.centerIds = idList;
    }
}
