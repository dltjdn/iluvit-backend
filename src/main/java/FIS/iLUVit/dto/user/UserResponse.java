package FIS.iLUVit.dto.user;

import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private Auth auth;
}
