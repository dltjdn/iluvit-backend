package FIS.iLUVit.security;

import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
        String token = JWT.create()
                .withSubject("ILuvIt_AccessToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs * 4L))
                .withClaim("id", userDetails.getUser().getId())
                .sign(Algorithm.HMAC512(secretKey));
        return token;
    }

    public String createRefreshToken(Authentication authentication) {
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        String token = JWT.create()
                .withSubject("ILuvIt_RefreshToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationDateInMs * 1000L))
                .withClaim("id", userDetails.getUser().getId())
                .sign(Algorithm.HMAC512(secretKey));
        return token;
    }

    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        return jwt.getClaim("id").asLong();
    }

    public Boolean isExpired(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return false;
        } catch (TokenExpiredException e) {
            log.warn("[JwtVerificationException] token 기간 만료 : {}", e.getMessage());
            return true;
        } catch (JWTVerificationException e) {
            log.warn("[JWTVerificationException] token 파싱 실패 : {}", e.getMessage());
            return false;
        }
    }

    public void validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        } catch (JWTVerificationException e) {
            log.warn("[JWTVerificationException] token 파싱 실패 : {}", e.getMessage());
            throw e;
        }
    }


    public String addPrefix(String token) {
        return "Bearer " + token;
    }
}
