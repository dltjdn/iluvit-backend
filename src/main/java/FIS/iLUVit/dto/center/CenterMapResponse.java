package FIS.iLUVit.dto.center;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CenterMapResponse {
    private Long id;
    private String name;                    // 시설명
    private Double longitude;               // 경도
    private Double latitude;                // 위도

    @QueryProjection
    public CenterMapResponse(Long id, String name, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
