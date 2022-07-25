package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CenterSearchMapFilterDTO {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private KindOf kindOf;                  // 시설 종류

    private List<Long> centerIds = new ArrayList<>();

    @Builder
    public CenterSearchMapFilterDTO(double longitude, double latitude, KindOf kindOf, Integer distance, List<Long> idList) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.kindOf = kindOf;
        this.centerIds = idList;
    }
}
