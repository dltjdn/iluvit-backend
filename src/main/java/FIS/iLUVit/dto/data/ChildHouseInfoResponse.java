package FIS.iLUVit.dto.data;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.ClassInfo;
import FIS.iLUVit.domain.embeddable.TeacherInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChildHouseInfoResponse {
    private String centerName;              // 시설명
    private Area area;                      // 시도명, 시군구명
    private String estType;                 // 설립유형
    private String status;                  // 운영현황 (정상, 휴지, 폐지, 재개)
    private String owner;                   // 대표자명
    private String zipcode;                 // 우편번호
    private String homepage;                // 홈페이지 주소
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private String program;                 // 제공서비스
    private BasicInfra basicInfra;          // 기본시설
    private ClassInfo classInfo;            // 학급정보
    private TeacherInfo teacherInfo;        // 선생님 정보

}
