package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.CenterModifyReqeustDto;
import FIS.iLUVit.domain.embeddable.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("null")
@DiscriminatorColumn(name = "kindOf")
@Getter
@NoArgsConstructor
public class Center extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    private String name;                    // 시설명
    private String owner;                   // 대표자명
    private String director;                // 원장명
    private String estType;                 // 설립유형
    private String status;                  //
    private String estDate;                 // 개원일
    private String tel;                     // 전화번호
    private String homepage;                // 홈페이지 주소
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    private String address;                 // 주소
    private String zipcode;                 // 우편번호
    @Embedded
    private Area area;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private String offerService;            // 제공서비스 (, 로 구분)
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private LocalDate updateDate;           // 정보 업데이트 일자
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private Integer waitingNum;             // 원아 모집이 false 일때 대기자 수
    @Lob
    private String introText;               // 시설 소개글
    private Integer imgCnt;                 // 시설 이미지 개수 최대 20장
    private Integer videoCnt;               // 시설 동영상 갯수 최대 5개

    @Column(name="kindOf", insertable = false, updatable = false)
    private String kindOf;                  // 시설 종류

    @Embedded
    private ClassInfo classInfo;            // 학급정보
    @Embedded
    private TeacherInfo teacherInfo;        // 선생님 정보
    @Embedded
    private CostInfo costInfo;              // 보육료 정보
    @Embedded
    private BasicInfra basicInfra;          // 기본시설
    @Embedded
    private Theme theme;                    // 테마
    @Embedded
    private OtherInfo otherInfo;            // 지문등록 사업에서 사용하는 정보들 집합

    @OneToMany(mappedBy = "center")
    private List<Program> programs = new ArrayList<>();
    @OneToMany(mappedBy = "center")
    private List<AddInfo> addInfos = new ArrayList<>();

    public static Center createCenter(String name, String owner, String director, String estType, String status, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address,
                  String zipcode, Area area, Double longitude, Double latitude, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean signed, Boolean recruit, Integer waitingNum, String introText,
                  Integer imgCnt, Integer videoCnt, String kindOf, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme, OtherInfo otherInfo) {
        Center center = new Center();
        center.name = name;
        center.owner = owner;
        center.director = director;
        center.estType = estType;
        center.status = status;
        center.estDate = estDate;
        center.tel = tel;
        center.homepage = homepage;
        center.startTime = startTime;
        center.endTime = endTime;
        center.minAge = minAge;
        center.maxAge = maxAge;
        center.address = address;
        center.zipcode = zipcode;
        center.area = area;
        center.longitude = longitude;
        center.latitude = latitude;
        center.offerService = offerService;
        center.maxChildCnt = maxChildCnt;
        center.curChildCnt = curChildCnt;
        center.updateDate = updateDate;
        center.signed = signed;
        center.recruit = recruit;
        center.waitingNum = waitingNum;
        center.introText = introText;
        center.imgCnt = imgCnt;
        center.videoCnt = videoCnt;
        center.kindOf = kindOf;
        center.classInfo = classInfo;
        center.teacherInfo = teacherInfo;
        center.costInfo = costInfo;
        center.basicInfra = basicInfra;
        center.theme = theme;
        center.otherInfo = otherInfo;
        center.kindOf = "Kindergarten";
        return center;
    }

    @Builder(toBuilder = true)
    public Center(Long id, String name, String owner, String director, String estType, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String zipcode, Area area, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean recruit, String introText, Integer imgCnt, Integer videoCnt, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.director = director;
        this.estType = estType;
        this.estDate = estDate;
        this.tel = tel;
        this.homepage = homepage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.zipcode = zipcode;
        this.area = area;
        this.offerService = offerService;
        this.maxChildCnt = maxChildCnt;
        this.curChildCnt = curChildCnt;
        this.updateDate = updateDate;
        this.recruit = recruit;
        this.introText = introText;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.classInfo = classInfo;
        this.teacherInfo = teacherInfo;
        this.costInfo = costInfo;
        this.basicInfra = basicInfra;
        this.theme = theme;
    }

    public Center update(CenterModifyReqeustDto requestDto) {
        return null;
    }

}
