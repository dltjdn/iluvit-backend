package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.QCenter;
import FIS.iLUVit.domain.embeddable.Area;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CenterAndDistance {
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
    private Area area;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private Double distance;

    public CenterAndDistance(Long id, String name, String owner, String director, String estType, String tel, String startTime, String endTime, Integer minAge, Integer maxAge, String address, Area area, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.director = director;
        this.estType = estType;
        this.tel = tel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public CenterAndDistance(Center center, double longitude, double latitude){
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.director = director;
        this.estType = estType;
        this.tel = tel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double calculateDistance(double longitude, double latitude){
        double theta = this.longitude - longitude;
        double dist = Math.sin(deg2rad(this.latitude)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(this.latitude)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;
        distance = dist;
        return (dist);
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
