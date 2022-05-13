package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CenterInfoResponseDto {
    private Long id;
    private String name;                    // 시설명
    private String estType;                 // 설립유형
    private String estDate;                 // 개원일
    private String tel;                     // 전화번호
    private String homepage;                // 홈페이지 주소
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    private String address;                 // 주소
    private Area area;
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private String offerService;            // 제공서비스 (, 로 구분)
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private LocalDate updateDate;           // 정보 업데이트 일자
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private String introText;               // 시설 소개글
    private Integer imgCnt;                 // 시설 이미지 개수 최대 20장
    private Integer videoCnt;               // 시설 동영상 갯수 최대 5개
    private String kindOf;                  // 시설 종류
    private ClassInfo classInfo;            // 학급정보
    private TeacherInfo teacherInfo;        // 선생님 정보
    private CostInfo costInfo;              // 보육료 정보
    private BasicInfra basicInfra;          // 기본시설
    private Theme theme;                    // 테마
    private List<String> programs = new ArrayList<>();
    private List<String> addInfos = new ArrayList<>();

    public CenterInfoResponseDto(Center center){
        this.id = center.getId();
        this.name = center.getName();
        this.estType = center.getEstType();
        this.estDate = center.getEstDate();
        this.tel = center.getTel();
        this.homepage = center.getHomepage();
        this.startTime = center.getStartTime();
        this.endTime = center.getEndTime();
        this.minAge = center.getMinAge();
        this.maxAge = center.getMaxAge();
        this.address = center.getAddress();
        this.area = center.getArea();
        this.longitude = center.getLongitude();
        this.latitude = center.getLatitude();
        this.offerService = center.getOfferService();
        this.maxChildCnt = center.getMaxChildCnt();
        this.curChildCnt = center.getCurChildCnt();
        this.updateDate = center.getUpdateDate();
        this.signed = center.getSigned();
        this.recruit = center.getRecruit();
        this.introText = center.getIntroText();
        this.imgCnt = center.getImgCnt();
        this.videoCnt = center.getVideoCnt();
        this.kindOf = center.getKindOf();
        this.classInfo = center.getClassInfo();
        this.teacherInfo = center.getTeacherInfo();
        this.costInfo = center.getCostInfo();
        this.basicInfra = center.getBasicInfra();
        this.theme = center.getTheme();
        this.programs = center.getPrograms().stream().map(program -> program.getName()).collect(Collectors.toList());
    }
}
