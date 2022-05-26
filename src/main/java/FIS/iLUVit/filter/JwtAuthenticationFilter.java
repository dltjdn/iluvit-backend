package FIS.iLUVit.filter;


import FIS.iLUVit.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// /login 요청시 username, pwd 전송하면 스프링 시큐리티의 UsernamePasswordAuthenticationFilter 동작
// but formLogin.disable() 때문에 동작X => securityConfig addFilter(JwtAuthenticationFilter)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청시 로그인을 시도하기 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // username, password 받아서
            ObjectMapper om = new ObjectMapper();
            LoginRequest loginInfo = om.readValue(request.getInputStream(), LoginRequest.class);

            // 로그인 정보를 token화 시키고
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginInfo.getLoginId(), loginInfo.getPassword());

            // 로그인 시도를 해본다
            // authenticationManager로 로그인 시도를 하면 PrincipalDetailsService 호출되고 loadByUsername 함수 자동 실행
            // db 정보와 일치한다면 authentication 객체 리턴됨
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            return authentication;

        } catch (IOException e) {
             e.printStackTrace();
        }
        return null;
    }

    // attemptAuthentication 성공적으로 수행되면 authentication 객체가 담긴채로 해당 메서드 실행
    // JWT 만들어서 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // HMAC512 방식의 Hash 암호화
        String jwtToken = JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 30))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", principalDetails.getUser().getId())
                .sign(Algorithm.HMAC512("symmetricKey"));   // 대칭키 들어가 자리(노출 금지)

        response.addHeader("Authorization", "Bearer " + jwtToken);
        response.addHeader("Access-Control-Allow-Origin", "*");
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }
}
