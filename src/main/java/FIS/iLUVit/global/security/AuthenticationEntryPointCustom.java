package FIS.iLUVit.global.security;

import FIS.iLUVit.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("[AuthenticationEntryPointCustom] Unauthorized error : {}", authException.getMessage());
        ErrorResponse errorResponse;
        if (authException instanceof BadCredentialsException) {
            errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 잘못되었습니다.");
        } else {
            errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
