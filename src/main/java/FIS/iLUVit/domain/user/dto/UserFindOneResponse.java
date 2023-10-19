package FIS.iLUVit.domain.user.dto;

import FIS.iLUVit.domain.common.domain.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFindOneResponse {
    private Long id;
    private String nickname;
    private Auth auth;
}
