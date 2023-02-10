package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.embeddable.*;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CenterDetailRequest {
    @NotBlank(message = "시설명은 비어있지 않아야 합니다")
    private String name;                    // 시설명
    private String owner;                   // 대표자명
    @NotNull(message = "원장명은 비어있지 않아야 합니다")
    private String director;                // 원장명
    @NotBlank(message = "설립유형은 비어있지 않아야 합니다")
    private String estType;                 // 설립유형
    private String estDate;                 // 개원일
    @NotNull(message = "시설 전화번호는 비어있지 않아야 합니다")
    private String tel;                     // 전화번호
    private String homepage;                // 홈페이지 주소
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    @NotNull(message = "시설 주소는 비어있지 않아야 합니다")
    private String address;                 // 주소
    private String addressDetail;
    private String zipcode;                 // 우편번호
    @NotNull(message = "제공 서비스는 null 값이 아니여야 합니다")
    private String offerService;            // 제공서비스 (, 로 구분)
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    @NotNull(message = "원아 모집은 null 값이 아니여야 합니다")
    private Boolean recruit;                // 원아 모집중
    private String introText;               // 시설 소개글
    private ClassInfo classInfo;            // 학급정보
    private TeacherInfo teacherInfo;        // 선생님 정보
    private CostInfo costInfo;              // 보육료 정보
    private BasicInfra basicInfra;          // 기본시설
    @NotNull(message = "관심사는 null 값이 아니여야 합니다")
    private Theme theme;                    // 테마
    private List<String> programs = new ArrayList<>();
    private List<String> addInfos = new ArrayList<>();
}
