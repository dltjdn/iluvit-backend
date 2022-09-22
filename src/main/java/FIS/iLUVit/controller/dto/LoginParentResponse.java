package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.security.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginParentResponse extends LoginResponse {

    public LoginParentResponse(Parent parent) {
        super(parent);
    }
}

