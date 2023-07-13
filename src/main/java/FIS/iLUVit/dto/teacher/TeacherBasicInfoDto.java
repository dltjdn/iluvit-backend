package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.dto.user.UserBasicInfoDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Getter;

@Getter
public class TeacherBasicInfoDto extends UserBasicInfoDto {
    private Long centerId;
    private Approval approval;

    public TeacherBasicInfoDto(Long id, String nickName, Auth auth, Center center, Approval approval) {
        super(id, nickName, auth);
        if (center != null) {
            this.centerId = center.getId();
            this.approval = approval;
        }
    }
}
