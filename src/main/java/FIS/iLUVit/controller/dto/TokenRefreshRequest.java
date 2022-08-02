package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
