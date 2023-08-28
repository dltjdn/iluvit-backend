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
    private TeacherInfo teacherInfo;    // 선생님 정보
}
