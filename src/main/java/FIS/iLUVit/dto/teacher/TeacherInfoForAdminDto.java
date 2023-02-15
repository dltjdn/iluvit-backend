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
    private Long teacherId;
    private String name;
    private String nickName;
    private Approval approval;
    private Auth auth;
    private String profileImg;

    public TeacherInfoForAdminDto(Long id, String name, String nickName, Approval approval, Auth auth) {
        this.teacherId = id;
        this.name = name;
        this.nickName=nickName;
        this.approval = approval;
        this.auth = auth;
    }
}