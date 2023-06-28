package FIS.iLUVit.domain;

import FIS.iLUVit.dto.center.CenterDetailRequest;
import FIS.iLUVit.domain.embeddable.*;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("null")
@DiscriminatorColumn(name = "kindOf")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Center extends BaseImageEntity {
    @Id @GeneratedValue
    protected Long id;

    protected String name;                    // 시설명
    protected String owner;                   // 대표자명
    protected String director;                // 원장명
    protected String estType;                 // 설립유형
    protected String status;                  //
    protected String estDate;                 // 개원일
    protected String tel;                     // 전화번호
    protected String homepage;                // 홈페이지 주소
    protected String startTime;               // 운영시작시간
    protected String endTime;                 // 운영종료시간
    protected Integer minAge;                 // 시설이 관리하는 연령대
    protected Integer maxAge;                 //
    protected String address;                 // 주소
    protected String addressDetail;
    protected String zipcode;                 // 우편번호
    @Embedded
    protected Area area;
    protected Double longitude;               // 경도
    protected Double latitude;                // 위도
    protected String offerService;            // 제공서비스 (, 로 구분)
    protected Integer maxChildCnt;            // 정원
    protected Integer curChildCnt;            // 현원
    protected LocalDate updateDate;           // 정보 업데이트 일자
    protected Boolean signed;                 // 원장의 가입 유무
    protected Boolean recruit;                // 원아 모집중
    protected Integer waitingNum;             // 원아 모집이 false 일때 대기자 수
    @Lob
    protected String introText;               // 시설 소개글
    protected Integer videoCnt;               // 시설 동영상 갯수 최대 5개
    protected Integer score;                    // 시설 order By 기준 중 하나
    protected String addInfo;
    protected String program;

    @Column(name="kindOf", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected KindOf kindOf;                  // 시설 종류

    @Embedded
    protected ClassInfo classInfo;            // 학급정보
    @Embedded
    protected TeacherInfo teacherInfo;        // 선생님 정보
    @Embedded
    protected CostInfo costInfo;              // 보육료 정보
    @Embedded
    protected BasicInfra basicInfra;          // 기본시설
    @Embedded
    protected Theme theme;                    // 테마
    @Embedded
    protected OtherInfo otherInfo;            // 지문등록 사업에서 사용하는 정보들 집합

//    @OneToMany(mappedBy = "center")
//    protected List<Presentation> presentations = new ArrayList<>();
    @OneToMany(mappedBy = "center")
    protected List<Teacher> teachers = new ArrayList<>();
    @OneToMany(mappedBy = "center")
    protected List<Review> reviews = new ArrayList<>();
//    @OneToMany(mappedBy = "center")
//    protected List<Board> boards = new ArrayList<>();
//
    @OneToMany(mappedBy = "center")
    protected List<Child> children = new ArrayList<>();
    @OneToMany(mappedBy = "center")
    protected List<Prefer> prefers = new ArrayList<>();

    @Builder
    public Center(Long id, String name, String owner, String director, String estType, String status, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String addressDetail, String zipcode, Area area, Double longitude, Double latitude, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean signed, Boolean recruit, Integer waitingNum, String introText, Integer imgCnt, Integer videoCnt, Integer score, String addInfo, String program, KindOf kindOf, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme, OtherInfo otherInfo, String infoImagePath, String profileImagePath) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.director = director;
        this.estType = estType;
        this.status = status;
        this.estDate = estDate;
        this.tel = tel;
        this.homepage = homepage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
        this.offerService = offerService;
        this.maxChildCnt = maxChildCnt;
        this.curChildCnt = curChildCnt;
        this.updateDate = updateDate;
        this.signed = signed;
        this.recruit = recruit;
        this.waitingNum = waitingNum;
        this.introText = introText;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.score = score;
        this.addInfo = addInfo;
        this.program = program;
        this.kindOf = kindOf;
        this.classInfo = classInfo;
        this.teacherInfo = teacherInfo;
        this.costInfo = costInfo;
        this.basicInfra = basicInfra;
        this.theme = theme;
        this.otherInfo = otherInfo;
        this.profileImagePath = profileImagePath;
        this.infoImagePath = infoImagePath;
    }

    public void update(CenterDetailRequest requestDto, Double longitude, Double latitude, String sido, String sigungu) {
        this.name = requestDto.getName();
        this.owner = requestDto.getOwner();
        this.director = requestDto.getDirector();
        this.estType = requestDto.getEstType();
        this.estDate = requestDto.getEstDate();
        this.tel = requestDto.getTel();
        this.homepage = requestDto.getHomepage();
        this.startTime = requestDto.getStartTime();
        this.endTime = requestDto.getEndTime();
        this.minAge = requestDto.getMinAge();
        this.maxAge = requestDto.getMaxAge();
        this.address = requestDto.getAddress();
        this.addressDetail = requestDto.getAddressDetail();
        this.zipcode = requestDto.getZipcode();
        this.area.setSido(sido);
        this.area.setSigungu(sigungu);
        this.offerService = requestDto.getOfferService();
        this.maxChildCnt = requestDto.getMaxChildCnt();
        this.curChildCnt = requestDto.getCurChildCnt();
        this.recruit = requestDto.getRecruit();
        this.introText = requestDto.getIntroText();
        this.classInfo = requestDto.getClassInfo();
        this.teacherInfo = requestDto.getTeacherInfo();
        this.costInfo = requestDto.getCostInfo();
        this.basicInfra = requestDto.getBasicInfra();
        this.theme = requestDto.getTheme();
        this.addInfo = Center.encodeStringList(requestDto.getAddInfos());
        this.program = Center.encodeStringList(requestDto.getPrograms());
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void addScore(Score mode){
        // null 처리
        if(score == null) score = 0;

        if(mode == Score.GET){
            this.score += Score.GET.getScore();
        } else if (mode == Score.Review){
            this.score += Score.Review.getScore();
        }
    }


    public static String encodeStringList(List<String> infos){
        String encodedString = "";
        for (String info : infos) {
            encodedString += "<d>" + info + "</d>";
        }
        return encodedString;
    }

    public static List<String> decodeString(String encodeString){
        if(encodeString == null || encodeString.equals(""))
            return new ArrayList<>();
        List<String> decodedStrings = new ArrayList<>();
        String regEx = "<d>(.*?)</d>";
        Pattern pat = Pattern.compile(regEx);
        Matcher match = pat.matcher(encodeString);
        while (match.find()) {
            String group = match.group();
            decodedStrings.add(group.substring(3, group.length() - 4));
        }
        return decodedStrings;
    }

}
