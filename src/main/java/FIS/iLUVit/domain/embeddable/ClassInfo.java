package FIS.iLUVit.domain.embeddable;

import javax.persistence.Embeddable;

@Embeddable
public class ClassInfo {
    // 연령별 학급수 (만 0 ~ 5세) 유치원은 (만3 ~ 5세)
    private Long class_0;
    private Long class_1;
    private Long class_2;
    private Long class_3;
    private Long class_4;
    private Long class_5;

    // 연령별 아이수 (만 0 ~ 5세) 유치원은 (만3 ~ 5세)
    private Long child_0;
    private Long child_1;
    private Long child_2;
    private Long child_3;
    private Long child_4;
    private Long child_5;

    // 특수 아동 총원수
    private Long child_spe;
}
