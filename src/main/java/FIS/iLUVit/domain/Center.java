package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.ClassInfo;

import javax.persistence.*;

@Entity
@Inheritance
public class Center {
    @Id @GeneratedValue
    private Long id;

    private String name;        // 시설명
    private String owner;       // 대표자명
    private String director;    // 원장명
    private String estType;     // 설립유형
    private String estDate;     // 개원일
    private String tel;         // 전화번호
    private String homepage;    // 홈페이지 주소
    private String startTime;   // 운영시작시간
    private String endTime;     // 운영종료시간
    private String address;     // 주소
    private String offerService;// 제공서비스 (, 로 구분)
    @Embedded
    private ClassInfo classInfo;// 학급정보
}
