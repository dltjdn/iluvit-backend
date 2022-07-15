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
    private Long id;
    private String name;                    // 시설명
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private Integer waitingNum;             // 원아 모집이 false 일때 대기자 수
    private String profileImage;
    private Double starAverage;
    private Boolean prefer;

    public CenterBannerDto(Long id, String name, Integer maxChildCnt, Integer curChildCnt, Boolean signed, Boolean recruit, Integer waitingNum, Double starAverage, Long preferId, String profileImage) {
        this.id = id;
        this.name = name;
        this.maxChildCnt = maxChildCnt;
        this.curChildCnt = curChildCnt;
        this.signed = signed;
        this.recruit = recruit;
        this.waitingNum = waitingNum;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
        this.prefer = preferId == null ? false : true;
    }

    public CenterBannerDto(Long id, String name, Integer maxChildCnt, Integer curChildCnt, Boolean signed, Boolean recruit, Integer waitingNum, Double starAverage, String profileImage) {
        this.id = id;
        this.name = name;
        this.maxChildCnt = maxChildCnt;
        this.curChildCnt = curChildCnt;
        this.signed = signed;
        this.recruit = recruit;
        this.waitingNum = waitingNum;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
    }
}
