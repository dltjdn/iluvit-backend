package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateAuthNumRequest {
    String phoneNum;
    String authNum;
    AuthKind authKind;
}
