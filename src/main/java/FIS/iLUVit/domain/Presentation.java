package FIS.iLUVit.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Presentation {
    @Id @GeneratedValue
    private Long id;

    private String start_date;          // 설명회 신청 기간
    private String end_date;
    private String place;               // 설명회 장소
    @Lob
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개
}
