package FIS.iLUVit.domain.user.dto;

import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordUpdateRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String originPwd;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String newPwd;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String newPwdCheck;
}
