package FIS.iLUVit.filter;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터(BasicAuthenticationFilter) 동작
// 권한이나 인증이 필요한 주소가 아니라면 해당 필터는 지나친다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    @Autowired
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader("Authorization");

        // header에 토큰이 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            // 토크이 없다면 별도 처리없이 해당 필터 넘어감
            chain.doFilter(request, response);
            return;
        }

        // 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        Long id;
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("symmetricKey")).build().verify(jwtToken);
            id = decodedJWT.getClaim("id").asLong();
        } catch (JWTVerificationException e) {
            chain.doFilter(request, response);
            return ;
        }

        // 서명이 정상적인지 확인
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new TokenExpiredException("인증되지 않은 사용자입니다.");
        }

        // Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 권한 구분을 위해 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장 - 일회성으로 사용하기 때문에 세션에 저장해도 됨
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 최신화
        response.addHeader("Authorization", "Bearer " + createToken(user));

        // 다음 필터로 진행
        chain.doFilter(request, response);
    }

    // HMAC512 방식의 Hash 암호화
    private String createToken(User user) {
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 30))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("symmetricKey"));
    }
}
