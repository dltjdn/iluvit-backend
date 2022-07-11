package FIS.iLUVit.domain.embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfra {
    private Boolean hasBus = false;                 // 통합차량 운영 여부
    private Boolean hasPlayground = false;          // 놀이터 유무
    private Boolean hasCCTV = false;                // CCTV 유무
    private Boolean hasSwimPool = false;            // 수영장 유무
    private Boolean hasBackpack = false;            // 가방 유무
    private Boolean hasUniform = false;             // 단체복 유무
    private Boolean hasKidsNote = false;            // 키즈노트 사용 유무
    private Boolean hasHandWriteNote = false;       // 수기알림장 유무
    private Boolean hasPhysics = false;             // 체육시설 있는지

    private Integer busCnt = 0;                 // 통합차량 수
    private Integer buildingYear = 0;           // 건축년도 ( 이걸로 신축 여부 판단하기 )
    private Integer cctvCnt = 0;                // CCTV 갯수
}
