package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.security.LoginResponse;
import lombok.Data;

@Data
public class LoginTeacherResponse extends LoginResponse {

    private Long center_id;

    public LoginTeacherResponse(Long id, String nickName, Auth auth, Center center) {
        super(id, nickName, auth);
        if (center != null) {
            this.center_id = center.getId();
        }
    }
}
