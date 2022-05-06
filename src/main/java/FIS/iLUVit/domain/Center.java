package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("null")
@DiscriminatorColumn
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
    private String sido;                    // 시도
    private String sigungu;                 // 시군구
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


}
