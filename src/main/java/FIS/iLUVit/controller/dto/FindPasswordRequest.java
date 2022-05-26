package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.Data;

@Data
public class FindPasswordRequest {
    private String loginId;
    private String phoneNum;
    private String AuthNum;
    private String newPwd;
    private String newPwdCheck;
}
