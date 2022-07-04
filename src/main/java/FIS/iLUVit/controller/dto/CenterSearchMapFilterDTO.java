package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CenterSearchMapFilterDTO {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    private Theme theme;
    private Integer interestedAge;

    private KindOf kindOf;                  // 시설 종류
    private Integer distance;

    @Builder
    public CenterSearchMapFilterDTO(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.theme = theme;
        this.interestedAge = interestedAge;
        this.kindOf = kindOf;
        this.distance = distance;
    }
}
