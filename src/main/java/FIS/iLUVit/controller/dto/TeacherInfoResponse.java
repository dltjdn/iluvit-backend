package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;

@Data
public class TeacherInfoResponse extends UserInfoResponse {

    private Long centerId;

    public TeacherInfoResponse(Long id, String nickName, Auth auth, Center center) {
        super(id, nickName, auth);
        if (center != null) {
            centerId = center.getId();
        }
    }
}
