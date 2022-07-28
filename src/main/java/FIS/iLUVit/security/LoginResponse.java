package FIS.iLUVit.security;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String nickname;
    private Auth auth;
    private String jwt;
    private String refresh;

    public LoginResponse(Long id, String nickname, Auth auth) {
        this.id = id;
        this.nickname = nickname;
        this.auth = auth;
    }
}
