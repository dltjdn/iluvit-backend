package FIS.iLUVit.controller.dto;

import lombok.Data;

@Data
public class PatchPasswordRequest {
    private String originPwd;
    private String newPwd;
    private String newPwdCheck;
}
