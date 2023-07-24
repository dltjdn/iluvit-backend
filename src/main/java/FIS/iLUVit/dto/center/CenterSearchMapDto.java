package FIS.iLUVit.dto.center;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CenterSearchMapDto {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private Double distance;


    @Builder
    public CenterSearchMapDto(double longitude, double latitude, Double distance) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }
}
