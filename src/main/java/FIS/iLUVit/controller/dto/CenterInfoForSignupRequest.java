package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterInfoForSignupRequest {
    private String signupKind;
    private String sido;
    private String sigungu;
    private String centerName;
}
