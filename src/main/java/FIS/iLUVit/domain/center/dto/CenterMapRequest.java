package FIS.iLUVit.domain.center.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CenterMapRequest {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private Double distance;

    @Builder
    public CenterMapRequest(double longitude, double latitude, Double distance) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }
}
