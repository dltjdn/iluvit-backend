package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindPasswordRequest {
    private String loginId;
    private String phoneNum;
    private String authNum;
    private String newPwd;
    private String newPwdCheck;
}
