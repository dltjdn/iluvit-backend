package FIS.iLUVit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
