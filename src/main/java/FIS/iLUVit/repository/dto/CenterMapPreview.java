package FIS.iLUVit.repository.dto;

import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CenterMapPreview {
    private Long id;
    private String name;                    // 시설명
    private KindOf kindOf;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private String profileImage;

    @QueryProjection
    public CenterMapPreview(Long id, String name, KindOf kindOf, Double longitude, Double latitude, String profileImage) {
        this.id = id;
        this.name = name;
        this.kindOf = kindOf;
        this.longitude = longitude;
        this.latitude = latitude;
        this.profileImage = profileImage;
    }
}
