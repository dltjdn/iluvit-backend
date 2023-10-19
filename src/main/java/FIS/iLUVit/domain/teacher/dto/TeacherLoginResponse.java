package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.user.dto.UserLoginResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TeacherLoginResponse extends UserLoginResponse {

    @JsonProperty("center_id")
    private Long centerId;
    private Approval approval;

    public TeacherLoginResponse(Teacher teacher) {
        super(teacher);
        if (teacher.getCenter() != null) {
            this.centerId = teacher.getCenter().getId();
            this.approval = teacher.getApproval();
        }
    }
}
