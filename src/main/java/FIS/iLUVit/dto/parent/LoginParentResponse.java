package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.security.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginParentResponse extends LoginResponse {

    public LoginParentResponse(Parent parent) {
        super(parent);
    }
}

