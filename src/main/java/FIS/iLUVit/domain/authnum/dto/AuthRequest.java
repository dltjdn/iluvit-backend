package FIS.iLUVit.domain.authnum.dto;

import FIS.iLUVit.domain.authnum.domain.AuthKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    String phoneNum;
    String authNum;
    AuthKind authKind;
}
