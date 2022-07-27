package FIS.iLUVit.security;

import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${security.secretKey}")
    private String secretKey;

    @Value("${security.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${security.refreshExpirationDateInMs}")
    private int refreshExpirationDateInMs;

    public String createAccessToken(Authentication authentication) {
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        return JWT.create()
                .withSubject("ILuvIt")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .withClaim("id", userDetails.getUser().getId())
                .sign(Algorithm.HMAC512(secretKey));
    }


}
