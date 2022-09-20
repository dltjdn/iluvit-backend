package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.security.LoginResponse;
import lombok.Data;

@Data
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
