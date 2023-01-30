package FIS.iLUVit.dto.center;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CenterSearchMapDto {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private Double distance;

    private String searchContent;

    @Builder
    public CenterSearchMapDto(double longitude, double latitude, Double distance) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }
}
