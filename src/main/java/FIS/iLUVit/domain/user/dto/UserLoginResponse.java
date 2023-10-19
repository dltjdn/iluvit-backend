package FIS.iLUVit.domain.user.dto;

import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.common.domain.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private Long id;
    private String nickname;
    private Auth auth;
    private Boolean needTutorial;
    private String accessToken;
    private String refreshToken;

    public UserLoginResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickName();
        this.auth = user.getAuth();
        this.needTutorial = user.getCreatedDate().equals(user.getUpdatedDate());
    }
}
