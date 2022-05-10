package FIS.iLUVit.filter;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ErrorResponse;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.uesrdetails.PrincipalDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.el.parser.TokenMgrError;
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

// 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터(BasicAuthenticationFilter) 동작
// 권한이나 인증이 필요한 주소가 아니라면 해당 필터는 지나친다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader("Authorization");

        System.out.println("@@@@@@ JwtAuthorizationFilter 가동 @@@@@@@@@@@@@@@@@@@2");
        // header에 토큰이 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            // 토크이 없다면 별도 처리없이 해당 필터 넘어감
            chain.doFilter(request, response);
            return;
        }
        System.out.println("@@@@@@@@@@@@@ 토큰 있음 @@@@@@@@@@@@@@@@@@@2");

        // 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        Long id = JWT.require(Algorithm.HMAC512("symmetricKey")).build().verify(jwtToken).getClaim("id").asLong();


        // 서명이 정상적인지 확인
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new TokenExpiredException("인증되지 않은 사용자입니다.");
        }
        System.out.println("@@@@@@@@@@@@@ 정상적인 토큰임 @@@@@@@@@@@@@@@@@@@2");

        // Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 권한 구분을 위해 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장 - 일회성으로 사용하기 때문에 세션에 저장해도 됨
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        System.out.println("authentication = " + authentication.getAuthorities());
        authentication.getAuthorities().forEach(a->{
            System.out.println("a.getAuthority().toString() = " + a.getAuthority().toString());
        });
        // 다음 필터로 진행
        chain.doFilter(request, response);
    }
}
