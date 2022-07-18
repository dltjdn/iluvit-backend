package FIS.iLUVit.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterBannerDto {
    @Id
    @GeneratedValue
    private Long centerId;
    private String name;                    // 시설명
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private String profileImage;
    private Double starAverage;
    private Boolean prefer;

    public CenterBannerDto(Long centerId, String name, Boolean signed, Boolean recruit, Double starAverage, Long preferId, String profileImage) {
        this.centerId = centerId;
        this.name = name;
        this.signed = signed;
        this.recruit = recruit;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
        this.prefer = preferId != null;
    }

    public CenterBannerDto(Long centerId, String name, Boolean signed, Boolean recruit, Double starAverage, String profileImage) {
        this.centerId = centerId;
        this.name = name;
        this.signed = signed;
        this.recruit = recruit;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
        this.prefer = false;
    }
}
