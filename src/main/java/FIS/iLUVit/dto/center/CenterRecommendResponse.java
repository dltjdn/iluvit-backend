package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CenterRecommendResponse {
    private Long centerId;
    private String centerName;
    private String profileImage;

    public CenterRecommendResponse(Center center){
        this.centerId = center.getId();
        this.centerName = center.getName();
        this.profileImage = center.getProfileImagePath();
    }

}
