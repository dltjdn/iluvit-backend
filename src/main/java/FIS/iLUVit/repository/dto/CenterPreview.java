package FIS.iLUVit.repository.dto;

import FIS.iLUVit.domain.AddInfo;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Program;
import FIS.iLUVit.domain.embeddable.Area;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.List;

@Data
public class CenterPreview {
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
    private String profileImage;

    @QueryProjection
    public CenterPreview(Center center){
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
        this.area = center.getArea();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
    }
}
