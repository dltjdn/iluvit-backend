package FIS.iLUVit.dto.center;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CenterMapPreviewDto {
    private Long id;
    private String name;                    // 시설명
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private Boolean signed;                 // 원장 가입 유무

    @QueryProjection
    public CenterMapPreviewDto(Long id, String name, Double longitude, Double latitude, Boolean signed) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.signed = signed;
    }
}
