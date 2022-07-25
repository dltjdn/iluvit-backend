package FIS.iLUVit.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CenterRecommendDto {
    private Long centerId;
    private String centerName;
    private String profileImage;

    @QueryProjection
    public CenterRecommendDto(Long centerId, String centerName, String profileImage) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.profileImage = profileImage;
    }
}
