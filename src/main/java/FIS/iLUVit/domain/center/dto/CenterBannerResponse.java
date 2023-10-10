package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CenterBannerResponse {
    private Long centerId;
    private String name;                    // 시설명
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private String profileImage;
    private List<String> infoImages;
    private Double starAverage;
    @JsonProperty(value = "prefer")
    private Boolean isCenterBookmark;             // 시설 북마크 여부

    public static CenterBannerResponse of(Center center, List<String> infoImages, Boolean isCenterBookmark, Double starAverage){
        return CenterBannerResponse.builder()
                .centerId(center.getId())
                .name(center.getName())
                .signed(center.getSigned())
                .recruit(center.getRecruit())
                .profileImage(center.getProfileImagePath())
                .infoImages(infoImages)
                .starAverage(starAverage)
                .isCenterBookmark(isCenterBookmark)
                .build();
    }
}
