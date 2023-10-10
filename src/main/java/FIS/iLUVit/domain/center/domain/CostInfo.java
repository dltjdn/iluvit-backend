package FIS.iLUVit.domain.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 데이터 미리 세팅 안되어 있고 사용자가 회원 가입후 직접 작성
 */
@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CostInfo {
    private Integer cost0;              // 만 0세 부모 부담 보육료
    private Integer cost1;              // 만 1세 부모 부담 보육료
    private Integer cost2;              // 만 2세 부모 부담 보육료
    private Integer cost3;              // 만 3세 부모 부담 보육료
    private Integer cost4;              // 만 4세 부모 부담 보육료
    private Integer cost5;              // 만 5세 부모 부담 보육료
}
