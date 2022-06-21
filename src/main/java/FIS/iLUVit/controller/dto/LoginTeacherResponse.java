package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.filter.LoginResponse;

public class LoginTeacherResponse extends LoginResponse {


    public LoginTeacherResponse(Long id, String nickName, Auth auth) {
        super(id, nickName, auth);
    }
}
