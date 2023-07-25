package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
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

    @QueryProjection
    public CenterBookmarkResponse(Center center, Double starAverage){
        this.id = center.getId();
        this.name = center.getName();
        this.owner = center.getOwner();
        this.director = center.getDirector();
        this.estType = center.getEstType();
        this.tel = center.getTel();
        this.startTime = center.getStartTime();
        this.endTime = center.getEndTime();
        this.minAge = center.getMinAge();
        this.maxAge = center.getMaxAge();
        this.address = center.getAddress();
        this.addressDetail = center.getAddressDetail();
        this.area = center.getArea();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
        this.theme = center.getTheme();
        this.starAverage = starAverage;
        this.profileImage = center.getProfileImagePath();
    }

}
