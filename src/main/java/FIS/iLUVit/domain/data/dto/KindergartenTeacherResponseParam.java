package FIS.iLUVit.domain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class KindergartenTeacherResponseParam {
    private String centerName;          // 유치원명
    private Integer totalCnt;           // 근속년수로 계산된 교사 총수
    private Integer dur_1;              // 근속년수 1년 미만 교사수
    private Integer dur12;              // 근속년수 1~2
    private Integer dur24;              // 근속년수 2~4
    private Integer dur46;              // 근속년수 4~6
    private Integer dur6_;              // 근속년수 6~

    public static KindergartenTeacherResponseParam create(
            String centerName, Integer totalCnt, Integer dur_1, Integer dur12,
            Integer dur24, Integer dur46, Integer dur6_) {

        return KindergartenTeacherResponseParam.builder()
                .centerName(centerName)
                .totalCnt(totalCnt)
                .dur_1(dur_1)
                .dur12(dur12)
                .dur24(dur24)
                .dur46(dur46)
                .dur6_(dur6_)
                .build();
    }

}
