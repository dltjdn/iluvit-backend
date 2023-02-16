package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.security.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginParentResponse extends LoginResponse {

    public LoginParentResponse(Parent parent) {
        super(parent);
    }
}

