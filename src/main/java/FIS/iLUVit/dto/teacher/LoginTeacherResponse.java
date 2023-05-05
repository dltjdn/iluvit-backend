package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.security.LoginResponse;
import lombok.Getter;

@Getter
public class LoginTeacherResponse extends LoginResponse {

    private Long center_id;
    private Approval approval;

    public LoginTeacherResponse(Teacher teacher) {
        super(teacher);
        if (teacher.getCenter() != null) {
            this.center_id = teacher.getCenter().getId();
            this.approval = teacher.getApproval();
        }
    }
}
