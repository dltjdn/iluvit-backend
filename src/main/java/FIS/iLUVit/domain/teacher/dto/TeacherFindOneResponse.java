package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.user.dto.UserFindOneResponse;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import lombok.Getter;

@Getter
public class TeacherFindOneResponse extends UserFindOneResponse {
    private Long centerId;
    private Approval approval;

    public TeacherFindOneResponse(Long id, String nickName, Auth auth, Center center, Approval approval) {
        super(id, nickName, auth);
        if (center != null) {
            this.centerId = center.getId();
            this.approval = approval;
        }
    }
}
