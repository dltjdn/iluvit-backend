package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.Data;

@Data
public class FindPasswordRequest {
    private String loginId;
    private String phoneNum;
    private String authNum;
    private String newPwd;
    private String newPwdCheck;
}
