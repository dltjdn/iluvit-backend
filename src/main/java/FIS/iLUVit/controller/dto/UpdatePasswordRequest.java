package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String originPwd;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String newPwd;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String newPwdCheck;
}
