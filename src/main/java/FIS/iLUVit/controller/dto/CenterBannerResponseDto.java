package FIS.iLUVit.controller.dto;

import FIS.iLUVit.repository.dto.CenterBannerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CenterBannerResponseDto {

    private Long centerId;
    private String name;                    // 시설명
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private String profileImage;
    private List<String> infoImages;
    private Double starAverage;
    private Boolean prefer;

    public CenterBannerResponseDto(CenterBannerDto banner, List<String> infoImages) {
        this.centerId = banner.getCenterId();
        this.name = banner.getName();
        this.signed = banner.getSigned();
        this.recruit = banner.getRecruit();
        this.starAverage = banner.getStarAverage();
        this.profileImage = banner.getProfileImage();
        this.infoImages = infoImages;
        this.prefer = banner.getPrefer();
    }

    public CenterBannerResponseDto(Long centerId, String name, Boolean signed, Boolean recruit, Double starAverage, Long preferId, String profileImage, List<String> infoImages) {
        this.centerId = centerId;
        this.name = name;
        this.signed = signed;
        this.recruit = recruit;
        this.starAverage = starAverage;
        this.infoImages = infoImages;
        this.profileImage = profileImage;
        this.prefer = preferId != null;
    }
}
