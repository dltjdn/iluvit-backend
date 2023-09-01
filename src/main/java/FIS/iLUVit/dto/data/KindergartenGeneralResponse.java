package FIS.iLUVit.dto.data;

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
    private Integer class_3;                // 만3세 반수
    private Integer class_4;                // 만4세 반수
    private Integer class_5;                // 만5세 반수
    private Integer child_3;                // 만3세 아동수
    private Integer child_4;                // 만4세 아동수
    private Integer child_5;                // 만5세 아동수
    private Integer child_spe;              // 특수장애 아동수
}
