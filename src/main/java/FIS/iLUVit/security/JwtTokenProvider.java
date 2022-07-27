package FIS.iLUVit.security;

import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.secretKey}")
    private String secretKey;

    @Value("${security.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${security.refreshExpirationDateInMs}")
    private int refreshExpirationDateInMs;

    public String createAccessToken(PrincipalDetails principalDetails) {
        return JWT.create().withClaim()
    }


}
