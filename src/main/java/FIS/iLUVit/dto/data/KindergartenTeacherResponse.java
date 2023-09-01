package FIS.iLUVit.dto.data;

import FIS.iLUVit.domain.embeddable.TeacherInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class KindergartenTeacherResponse {
    private String centerName;          // 유치원명
    private Integer totalCnt;           // 근속년수로 계산된 교사 총수
    private Integer dur_1;              // 근속년수 1년 미만 교사수
    private Integer dur12;              // 근속년수 1~2
    private Integer dur24;              // 근속년수 2~4
    private Integer dur46;              // 근속년수 4~6
    private Integer dur6_;              // 근속년수 6~

}
