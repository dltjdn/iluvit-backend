package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterModifyRequestDto {
    private String name;                    // 시설명
    private String owner;                   // 대표자명
    private String director;                // 원장명
    private String estType;                 // 설립유형
    private String estDate;                 // 개원일
    private String tel;                     // 전화번호
    private String homepage;                // 홈페이지 주소
    private String startTime;               // 운영시작시간
    private String endTime;                 // 운영종료시간
    private Integer minAge;                 // 시설이 관리하는 연령대
    private Integer maxAge;                 //
    private String address;                 // 주소
    private String sido;
    private String sigungu;
    private String zipcode;                 // 우편번호
    private String offerService;            // 제공서비스 (, 로 구분)
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private Boolean recruit;                // 원아 모집중
    private String introText;               // 시설 소개글
    private ClassInfo classInfo;            // 학급정보
    private TeacherInfo teacherInfo;        // 선생님 정보
    private CostInfo costInfo;              // 보육료 정보
    private BasicInfra basicInfra;          // 기본시설
    private Theme theme;                    // 테마
}
