package FIS.iLUVit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
public class ChildHouseInfoResponse {
    @XmlElement(name = "crname")
    private String centerName;              // 시설명
    @XmlElement(name = "sidoname")
    private String sido;                    // 시도명
    @XmlElement(name = "sigunname")
    private String sigungu;                 // 시군구명
    @XmlElement(name = "crtypename")
    private String estType;                 // 설립유형
    @XmlElement(name = "crstatusname")
    private String status;                  // 운영현황 (정상, 휴지, 폐지, 재개)
    @XmlElement(name = "CRREPNAME")
    private String owner;                   // 대표자명
    @XmlElement(name = "zipcode")
    private String zipcode;                 // 우편번호
    @XmlElement(name = "crhome")
    private String homepage;                // 홈페이지 주소
    @XmlElement(name = "crcapat")
    private Integer maxChildCnt;            // 정원
    @XmlElement(name = "crchcnt")
    private Integer curChildCnt;            // 현원
    @XmlElement(name = "crspec")
    private String program;                 // 제공서비스
    @XmlElement(name = "crcargbname")
    private String hasBus;                  // 통합차량 운영 여부
    @XmlElement(name = "plgrdco")
    private Integer playGroundCnt;          // 놀이터 수
    @XmlElement(name = "cctvinstlcnt")
    private Integer cctvCnt;                // CCTV 총 설치 수
    @XmlElement(name = "CLASS_CNT_00")
    private Integer class_0;                // 만0세 반수
    @XmlElement(name = "CLASS_CNT_01")
    private Integer class_1;                // 만1세 반수
    @XmlElement(name = "CLASS_CNT_02")
    private Integer class_2;                // 만2세 반수
    @XmlElement(name = "CLASS_CNT_03")
    private Integer class_3;                // 만3세 반수
    @XmlElement(name = "CLASS_CNT_04")
    private Integer class_4;                // 만4세 반수
    @XmlElement(name = "CLASS_CNT_05")
    private Integer class_5;                // 만5세 반수
    @XmlElement(name = "CHILD_CNT_00")
    private Integer child_0;                // 만0세 아동수
    @XmlElement(name = "CHILD_CNT_01")
    private Integer child_1;                // 만1세 아동수
    @XmlElement(name = "CHILD_CNT_02")
    private Integer child_2;                // 만2세 아동수
    @XmlElement(name = "CHILD_CNT_03")
    private Integer child_3;                // 만3세 아동수
    @XmlElement(name = "CHILD_CNT_04")
    private Integer child_4;                // 만4세 아동수
    @XmlElement(name = "CHILD_CNT_05")
    private Integer child_5;                // 만5세 아동수
    @XmlElement(name = "CHILD_CNT_SP")
    private Integer child_spe;              // 특수장애 아동수
    @XmlElement(name = "EM_CNT_0Y")
    private Double dur_1;                  // 근속년수 1년 미만 교사수
    @XmlElement(name = "EM_CNT_1Y")
    private Double dur12;                  // 근속년수 1년 이상 2년 미만 교사수
    @XmlElement(name = "EM_CNT_2Y")
    private Double dur24;                  // 근속년수 2년 이상 4년 미만 교사수
    @XmlElement(name = "EM_CNT_4Y")
    private Double dur46;                  // 근속년수 4년 이상 6년 미만 교사수
    @XmlElement(name = "EM_CNT_6Y")
    private Double dur6_;                  // 근속년수 6년 이상 교사수
}
