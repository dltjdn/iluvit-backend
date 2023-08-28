package FIS.iLUVit.dto.data;

import FIS.iLUVit.domain.embeddable.ClassInfo;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
@AllArgsConstructor
public class KindergartenGeneralResponse {

    private String centerName;      // 시설명
    private String estType;         // 설립유형
    private String owner;           // 대표자명
    private String director;        // 원장명
    private String estDate;         // 개원일
    private String startTime;       // 운영시작시간
    private String endTime;         // 운영종료시간
    private String homepage;        // 홈페이지 주소
    private Integer maxChildCnt;    // 정원
    private ClassInfo classInfo;    // 학급정보

}
