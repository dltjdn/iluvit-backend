package FIS.iLUVit.dto.auth;

import FIS.iLUVit.domain.iluvit.enumtype.AuthKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthNumRequest {
    String phoneNum;
    String authNum;
    AuthKind authKind;
}
