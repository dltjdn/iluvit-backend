package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.Data;

@Data
public class AuthenticateAuthNumRequest {
    String phoneNum;
    String authNum;
    AuthKind authKind;
}
