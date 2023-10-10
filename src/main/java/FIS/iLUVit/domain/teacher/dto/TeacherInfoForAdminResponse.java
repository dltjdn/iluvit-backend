package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherInfoForAdminResponse {
    private Long teacherId;
    private String name;
    private String nickName;
    private Approval approval;
    private Auth auth;
    private String profileImg;

    public TeacherInfoForAdminResponse(Teacher teacher, String profileImg) {
        this.teacherId =teacher.getId();
        this.name = teacher.getName();
        this.nickName=teacher.getNickName();
        this.approval = teacher.getApproval();
        this.auth = teacher.getAuth();
        this.profileImg = profileImg;
    }
}
