package FIS.iLUVit.domain.embeddable;

import javax.persistence.Embeddable;

@Embeddable
public class BasicInfra {
    private Boolean hasBus;                 // 통합차량 운영 여부
    private Boolean hasPlayground;          // 놀이터 유무
    private Boolean hasCCTV;                // CCTV 유무
    private Boolean hasSwimPool;            // 수영장 유무
    private Boolean hasBackpack;            // 가방 유무
    private Boolean hasUniform;             // 단체복 유무
    private Boolean hasKidsNote;            // 키즈노트 사용 유무
    private Boolean hasHandWriteNote;       // 수기알림장 유무

    private Integer BusCnt;                 // 통합차량 수
    private Integer buildingYear;           // 건축년도 ( 이걸로 신축 여부 판단하기 )
    private Integer cctvCnt;                // CCTV 갯수
}
