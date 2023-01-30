package FIS.iLUVit.dto.parent;

import FIS.iLUVit.dto.user.UserResponse;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;

@Data
public class ParentResponse extends UserResponse {

    public ParentResponse(Long id, String nickName, Auth auth) {
        super(id, nickName, auth);
    }
}
