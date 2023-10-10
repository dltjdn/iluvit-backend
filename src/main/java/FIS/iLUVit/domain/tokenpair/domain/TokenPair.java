package FIS.iLUVit.domain.tokenpair.domain;

import FIS.iLUVit.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenPair {

    @Id @GeneratedValue
    private Long id;
    private String accessToken;
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public static TokenPair createTokenPair(String accessToken, String refreshToken, User user) {
        TokenPair tokenPair = new TokenPair();
        tokenPair.accessToken = accessToken;
        tokenPair.refreshToken = refreshToken;
        tokenPair.user = user;
        return tokenPair;
    }

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
