package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.*;
import FIS.iLUVit.domain.center.domain.KindOf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CenterFindResponse {
    private Long id;
    private String name;                    // 시설명
    private String estType;                 // 설립유형
    private String estDate;                 // 개원일
    private String tel;                     // 전화번호
    private String director;                // 원장
    private String homepage;                // 홈페이지 주소
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 최소 나이
    private Integer maxAge;                 // 시설이 관리하는 최대 나이
    private String address;                 // 주소
    private String addressDetail;           // 상세 주소
    private Area area;                      // 시도, 시군구
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
    private KindOf kindOf;                  // 시설 종류
    private ClassInfo classInfo;            // 학급정보
    private TeacherInfo teacherInfo;        // 선생님 정보
    private CostInfo costInfo;              // 보육료 정보
    private BasicInfra basicInfra;          // 기본시설
    private Theme theme;                    // 테마
    private String profileImage;
    private List<String> infoImages;
    private List<String> programs;
    private List<String> addInfos;

    public static CenterFindResponse of(Center center, List<String> infoImages){
        return CenterFindResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .estType(center.getEstType())
                .estDate(center.getEstDate())
                .tel(center.getTel())
                .director(center.getDirector())
                .homepage(center.getHomepage())
                .startTime(center.getStartTime())
                .endTime(center.getEndTime())
                .minAge(center.getMinAge())
                .maxAge(center.getMaxAge())
                .address(center.getAddress())
                .addressDetail(center.getAddressDetail())
                .area(center.getArea())
                .longitude(center.getLongitude())
                .latitude(center.getLatitude())
                .offerService(center.getOfferService())
                .maxChildCnt(center.getMaxChildCnt())
                .curChildCnt(center.getCurChildCnt())
                .updateDate(center.getUpdateDate())
                .signed(center.getSigned())
                .recruit(center.getRecruit())
                .introText(center.getIntroText())
                .imgCnt(center.getImgCnt())
                .videoCnt(center.getVideoCnt())
                .kindOf(center.getKindOf())
                .classInfo(center.getClassInfo())
                .teacherInfo(center.getTeacherInfo())
                .costInfo(center.getCostInfo())
                .basicInfra(center.getBasicInfra())
                .theme(center.getTheme())
                .programs(Center.decodeString(center.getProgram()))
                .addInfos(Center.decodeString(center.getAddInfo()))
                .profileImage(center.getProfileImagePath())
                .infoImages(infoImages)
                .build();
    }

}
