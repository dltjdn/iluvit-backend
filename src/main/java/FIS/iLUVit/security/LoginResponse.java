package FIS.iLUVit.security;

import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
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
    private Boolean needTutorial;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickName();
        this.auth = user.getAuth();
        this.needTutorial = user.getCreatedDate().equals(user.getUpdatedDate());
    }
}
