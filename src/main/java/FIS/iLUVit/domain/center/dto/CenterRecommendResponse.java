package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CenterRecommendResponse {
    private Long centerId;
    private String centerName;
    private String profileImage;

    public static CenterRecommendResponse from (Center center){
        return CenterRecommendResponse.builder()
                .centerId(center.getId())
                .centerName(center.getName())
                .profileImage(center.getProfileImagePath())
                .build();
    }

}
