package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.user.dto.UserInfoResponse;
import lombok.Getter;

@Getter
public class TeacherLoginResponse extends UserInfoResponse {

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
