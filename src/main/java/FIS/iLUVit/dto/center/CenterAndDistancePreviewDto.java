package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;


@Data
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
    private Theme theme;
    private Double distance;
    private Double starAverage;
    private String profileImage;
    private Boolean prefer;

    @QueryProjection
    public CenterAndDistancePreviewDto(Center center, Double starAverage, Long prefer){
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
        this.prefer = prefer != null;
    }

    @Builder
    @QueryProjection
    public CenterAndDistancePreviewDto(Double distance, Long id, String name, KindOf kindOf, String estType, String tel, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String addressDetail, Double longitude, Double latitude, Theme theme, Double starAverage, String profileImage, Long prefer) {
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
        this.prefer = prefer != null;
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
        this.prefer = false;
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
        this.prefer = false;
    }

    public Double calculateDistance(double longitude, double latitude){
        double theta = this.longitude - longitude;
        double dist = Math.sin(deg2rad(this.latitude)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(this.latitude)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;
        this.distance = dist/1000.0;               //km 단위로 끊음
        return this.distance;
    }

    // This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
