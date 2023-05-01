package FIS.iLUVit.dto.center;

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
    private Double starAverage;         // 센터 리뷰 평균 평점
    private Boolean prefer;             // 내가 북마크한 시설인지  여부

    public CenterBannerResponse(Long centerId, String name, Boolean signed, Boolean recruit, Double starAverage, Boolean IsPrefer, String profileImage, List<String> infoImages) {
        this.centerId = centerId;
        this.name = name;
        this.signed = signed;
        this.recruit = recruit;
        this.starAverage = starAverage;
        this.infoImages = infoImages;
        this.profileImage = profileImage;
        this.prefer = IsPrefer;
    }
}
