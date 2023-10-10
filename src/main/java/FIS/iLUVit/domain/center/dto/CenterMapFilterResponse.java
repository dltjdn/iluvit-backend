package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.center.domain.KindOf;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CenterMapFilterResponse {
    private Long id;
    private String name;                    // 시설명
    private KindOf kindOf;                  // 시설 종류
    private String estType;                 // 설립유형
    private String tel;                     // 전화번호
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 최소 연령
    private Integer maxAge;                 // 시설이 관리하는 최대 연령
    private String address;                 // 주소
    private String addressDetail;           // 상세 주소
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private String profileImage;            // 프로필 이미지
    private Theme theme;                    // 테마
    private Double distance;                // 시설과 내 현위치 간 거리
    private Double starAverage;// 시설 평점
    @JsonProperty(value="prefer")
    private Boolean isCenterBookmark;       // 시설 즐겨찾기 여부

    public static CenterMapFilterResponse of(Center center, Double distance, Double starAverage, Boolean isCenterBookmark){
        return CenterMapFilterResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .kindOf(center.getKindOf())
                .estType(center.getEstType())
                .tel(center.getTel())
                .startTime(center.getStartTime())
                .endTime(center.getEndTime())
                .minAge(center.getMinAge())
                .maxAge(center.getMaxAge())
                .address(center.getAddress())
                .addressDetail(center.getAddressDetail())
                .longitude(center.getLongitude())
                .latitude(center.getLatitude())
                .theme(center.getTheme())
                .profileImage(center.getProfileImagePath())
                .distance(distance)
                .starAverage(starAverage)
                .isCenterBookmark(isCenterBookmark)
                .build();
    }

}
