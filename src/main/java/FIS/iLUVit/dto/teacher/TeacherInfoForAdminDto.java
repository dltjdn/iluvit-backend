package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherInfoForAdminDto {
    private Long teacher_id;
    private String name;
    private Approval approval;
    private Auth auth;
    private String profileImg;

    public TeacherInfoForAdminDto(Long id, String name, Approval approval, Auth auth) {
        this.teacher_id = id;
        this.name = name;
        this.approval = approval;
        this.auth = auth;
    }
}
