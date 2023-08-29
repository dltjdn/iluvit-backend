package FIS.iLUVit.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthFindPasswordRequest {
    private String loginId;
    private String phoneNum;
    private String authNum;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String newPwd;
    private String newPwdCheck;
}
