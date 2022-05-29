package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.filter.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginParentResponse extends LoginResponse {

    private Integer interestAge;

    public LoginParentResponse(Long id, String nickname, Auth auth, Integer interestAge) {
        super(id, nickname, auth);
        this.interestAge = interestAge;
    }
}

