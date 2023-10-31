package FIS.iLUVit.domain.centerbookmark.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.domain.Area;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.center.dto.CenterBannerResponse;
import FIS.iLUVit.domain.centerbookmark.controller.CenterBookmarkController;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CenterBookmarkResponse {
    private Long id;
    private String name;                    // 시설명
    private String owner;                   // 대표자명
    private String director;                // 원장명
    private String estType;                 // 설립유형
    private String tel;                     // 전화번호
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    private String address;                 // 주소
    private String addressDetail;
    private Area area;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private Theme theme;
    private String profileImage;
    private Double starAverage;

    public static CenterBookmarkResponse of(Center center, Double starAverage){
        return CenterBookmarkResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .owner(center.getOwner())
                .director(center.getDirector())
                .estType(center.getEstType())
                .tel(center.getTel())
                .startTime(center.getStartTime())
                .endTime(center.getEndTime())
                .minAge(center.getMinAge())
                .maxAge(center.getMaxAge())
                .address(center.getAddress())
                .addressDetail(center.getAddressDetail())
                .area(center.getArea())
                .longitude(center.getLongitude())
                .latitude(center.getLatitude())
                .theme(center.getTheme())
                .profileImage(center.getProfileImagePath())
                .starAverage(starAverage)
                .build();
    }

}
