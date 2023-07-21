package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;


@Getter
public class CenterAndDistancePreviewDto {
    private Long id;
    private String name;                    // 시설명
    private KindOf kindOf;

    private String estType;                 // 설립유형
    private String tel;                     // 전화번호
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    private String address;                 // 주소
    private String addressDetail;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private String profileImage;
    private Theme theme;
    private Double distance;
    private Double starAverage;
    private Boolean hasPrefer;

    public CenterAndDistancePreviewDto( Center center, Double distance, Double starAverage, Boolean hasPrefer) {
        this.id = center.getId();
        this.name = center.getName();
        this.kindOf = center.getKindOf();
        this.estType = center.getEstType();
        this.tel = center.getTel();
        this.startTime = center.getStartTime();
        this.endTime = center.getEndTime();
        this.minAge = center.getMinAge();
        this.maxAge = center.getMaxAge();
        this.address = center.getAddress();
        this.addressDetail = center.getAddressDetail();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
        this.theme = center.getTheme();
        this.profileImage = center.getProfileImagePath();
        this.distance = distance;
        this.starAverage = starAverage;
        this.hasPrefer = hasPrefer;
    }

    @QueryProjection
    public CenterAndDistancePreviewDto(Center center, Double starAverage, Long hasPrefer){
        this.id = center.getId();
        this.name = center.getName();
        this.estType = center.getEstType();
        this.tel = center.getTel();
        this.startTime = center.getStartTime();
        this.endTime = center.getEndTime();
        this.minAge = center.getMinAge();
        this.maxAge = center.getMaxAge();
        this.address = center.getAddress();
        this.addressDetail = center.getAddressDetail();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
        this.starAverage = starAverage;
        this.theme = center.getTheme();
        this.profileImage = center.getProfileImagePath();
        this.hasPrefer = hasPrefer != null;
    }

    @Builder
    @QueryProjection
    public CenterAndDistancePreviewDto(Double distance, Long id, String name, KindOf kindOf, String estType, String tel, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String addressDetail, Double longitude, Double latitude, Theme theme, Double starAverage, String profileImage, Long hasPrefer) {
        this.id = id;
        this.name = name;
        this.kindOf = kindOf;
        this.estType = estType;
        this.tel = tel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.addressDetail = addressDetail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.theme = theme;
        this.distance = distance;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
        this.hasPrefer = hasPrefer != null;
    }

    @QueryProjection
    public CenterAndDistancePreviewDto(Double distance, Long id, String name, KindOf kindOf, String estType, String tel, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String addressDetail, Double longitude, Double latitude, Theme theme, Double starAverage, String profileImage) {
        this.id = id;
        this.name = name;
        this.kindOf = kindOf;
        this.estType = estType;
        this.tel = tel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.addressDetail = addressDetail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.theme = theme;
        this.distance = distance;
        this.starAverage = starAverage;
        this.profileImage = profileImage;
        this.hasPrefer = false;
    }

    @QueryProjection
    public CenterAndDistancePreviewDto(Center center, Double starAverage, Double distance){
        this.id = center.getId();
        this.name = center.getName();
        this.estType = center.getEstType();
        this.tel = center.getTel();
        this.startTime = center.getStartTime();
        this.endTime = center.getEndTime();
        this.minAge = center.getMinAge();
        this.maxAge = center.getMaxAge();
        this.address = center.getAddress();
        this.addressDetail = center.getAddressDetail();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
        this.starAverage = starAverage;
        this.theme = center.getTheme();
        this.profileImage = center.getProfileImagePath();
        this.distance = distance;
        this.hasPrefer = false;
    }

}
