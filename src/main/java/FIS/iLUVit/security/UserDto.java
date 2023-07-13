package FIS.iLUVit.security;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private Auth auth;
    private Boolean needTutorial;
    private String accessToken;
    private String refreshToken;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickName();
        this.auth = user.getAuth();
        this.needTutorial = user.getCreatedDate().equals(user.getUpdatedDate());
    }
}
