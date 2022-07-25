package FIS.iLUVit.controller.dto;

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
    private Integer distance;

    private String searchContent;

    @Builder
    public CenterSearchMapDto(double longitude, double latitude, Integer distance) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }
}
