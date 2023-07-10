package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Prefer;
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
    private Boolean prefer;             // 시설 북마크 여부

    public CenterBannerResponse(Long centerId, String name, Boolean signed, Boolean recruit , Prefer prefer, String profileImage, Double starAverage, List<String> infoImages) {
        this.centerId = centerId;
        this.name = name;
        this.signed = signed;
        this.recruit = recruit;
        this.starAverage = starAverage;
        this.infoImages = infoImages;
        this.profileImage = profileImage;
        this.prefer = prefer != null;
    }
}
