package FIS.iLUVit.security;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import FIS.iLUVit.domain.Admin;
import FIS.iLUVit.repository.AdminRepository;
import FIS.iLUVit.security.uesrdetails.AdminDetails;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final JwtUtils jwtUtils;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, AdminRepository adminRepository, JwtUtils jwtUtils) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.jwtUtils = jwtUtils;
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

        String[] requestMode = request.getParameterValues("mode");
        log.error("**** {}", requestMode == null ? "none" : requestMode[0]);
        Boolean hasFisURI = requestMode == null ? false : requestMode[0].matches("fis");
        
        try {
            // 토큰을 검증해서 정상적인 사용자인지 확인
            String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

            if (hasFisURI) {
                Long id = jwtUtils.getAdminIdFromToken(jwtToken);

                Admin admin = adminRepository.findById(id).
                        orElseThrow(() -> new JWTVerificationException("유효하지 않은 토큰입니다(Invalid Admin)."));

                // Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
                AdminDetails adminDetails = new AdminDetails(admin);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        adminDetails, null, adminDetails.getAuthorities());

                // 권한 구분을 위해 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장 - 일회성으로 사용하기 때문에 세션에 저장해도 됨
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                Long id = jwtUtils.getUserIdFromToken(jwtToken);
    
                User user = userRepository.findById(id).
                        orElseThrow(() -> new JWTVerificationException("유효하지 않은 토큰입니다(Invalid User)."));

                // Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
                PrincipalDetails principalDetails = new PrincipalDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principalDetails, null, principalDetails.getAuthorities());

                // 권한 구분을 위해 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장 - 일회성으로 사용하기 때문에 세션에 저장해도 됨
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // signature, jwt 만료등 유효성 검사에 실패할 경우 -> 예외처리 해야됨
        } catch (JWTVerificationException e) {
            log.warn("[JwtAuthorizationFilter] token 파싱 실패 : {}", e.getMessage());
            throw e;
        }
        // 다음 필터로 진행
        chain.doFilter(request, response);
    }
}
