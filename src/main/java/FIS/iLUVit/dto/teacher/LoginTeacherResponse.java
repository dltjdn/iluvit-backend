package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.security.UserDto;
import lombok.Getter;

@Getter
public class LoginTeacherResponse extends UserDto {

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
