package FIS.iLUVit.domain.embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Embeddable
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Theme {
    private Boolean English;                // 양어
    private Boolean foreigner;              // 원어민
    private Boolean clean;                  // 청결
    private Boolean Buddhism;               // 불교
    private Boolean christianity;           // 기독교
    private Boolean catholic;               // 천주교
    private Boolean animal;                 // 동물
    private Boolean plant;                  // 식물
    private Boolean camping;                // 캠핑
    private Boolean nature;                 // 자연친화
    private Boolean art;                    // 미술
    private Boolean music;                  // 음악
    private Boolean math;                   // 수학
    private Boolean sport;                  // 체육
    private Boolean coding;                 // 코딩
    private Boolean manner;                 // 예절
    private Boolean genius;                 // 양재


}
