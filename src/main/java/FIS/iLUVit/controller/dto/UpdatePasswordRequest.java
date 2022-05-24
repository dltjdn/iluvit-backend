package FIS.iLUVit.controller.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String originPwd;
    private String newPwd;
    private String newPwdCheck;
}
