package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public static RefreshToken createRefreshToken(String token, User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.token = token;
        refreshToken.user = user;
        return refreshToken;
    }

    public void updateToken(String refresh) {
        this.token = refresh;
    }
}
