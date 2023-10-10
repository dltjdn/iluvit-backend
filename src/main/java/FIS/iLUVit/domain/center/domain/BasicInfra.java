package FIS.iLUVit.domain.center.domain;

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
    @Builder.Default
    private Boolean hasBus = false;                 // 통합차량 운영 여부
    @Builder.Default
    private Boolean hasPlayground = false;          // 놀이터 유무
    @Builder.Default
    private Boolean hasCCTV = false;                // CCTV 유무
    @Builder.Default
    private Boolean hasSwimPool = false;            // 수영장 유무
    @Builder.Default
    private Boolean hasBackpack = false;            // 가방 유무
    @Builder.Default
    private Boolean hasUniform = false;             // 단체복 유무
    @Builder.Default
    private Boolean hasKidsNote = false;            // 키즈노트 사용 유무
    @Builder.Default
    private Boolean hasHandWriteNote = false;       // 수기알림장 유무
    @Builder.Default
    private Boolean hasPhysics = false;             // 체육시설 있는지

    @Builder.Default
    private Integer busCnt = 0;                 // 통합차량 수
    @Builder.Default
    private Integer buildingYear = 0;           // 건축년도 ( 이걸로 신축 여부 판단하기 )
    @Builder.Default
    private Integer cctvCnt = 0;                // CCTV 갯수

    public void updateSafetyInfo(Boolean hasCCTV, Integer cctvCnt) {
        this.hasCCTV = hasCCTV;
        this.cctvCnt = cctvCnt;
    }

    public BasicInfra(Boolean hasBus, Boolean hasPlayground, Boolean hasCCTV, Integer cctvCnt) {
        this.hasBus = hasBus;
        this.hasPlayground = hasPlayground;
        this.hasCCTV = hasCCTV;
        this.cctvCnt = cctvCnt;
    }
}
