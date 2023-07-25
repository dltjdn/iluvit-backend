package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterBannerResponse {
    private Long centerId;
    private String name;                    // 시설명
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private String profileImage;
    private List<String> infoImages;
    private Double starAverage;
    private Boolean isCenterBookmark;             // 시설 북마크 여부

    public CenterBannerResponse(Center center, List<String> infoImages, Boolean isCenterBookmark, Double starAverage) {
        this.centerId = center.getId();
        this.name = center.getName();
        this.signed = center.getSigned();
        this.recruit = center.getRecruit();
        this.profileImage = center.getProfileImagePath();
        this.infoImages = infoImages;
        this.starAverage = starAverage;
        this.isCenterBookmark = isCenterBookmark;
    }
}
