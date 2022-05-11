package FIS.iLUVit.domain.embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassInfo {
    // 연령별 학급수 (만 0 ~ 5세) 유치원은 (만3 ~ 5세)
    private Integer class_0;
    private Integer class_1;
    private Integer class_2;
    private Integer class_3;
    private Integer class_4;
    private Integer class_5;

    // 연령별 아이수 (만 0 ~ 5세) 유치원은 (만3 ~ 5세)
    private Integer child_0;
    private Integer child_1;
    private Integer child_2;
    private Integer child_3;
    private Integer child_4;
    private Integer child_5;

    // 특수 아동 총원수
    private Integer child_spe;
}
