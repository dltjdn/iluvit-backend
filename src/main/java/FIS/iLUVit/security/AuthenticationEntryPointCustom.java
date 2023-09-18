package FIS.iLUVit.security;

import FIS.iLUVit.exception.BasicErrorResult;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
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
            errorResponse = ErrorResponse.from(BasicErrorResult.BAD_CREDENTIALS);
        } else {
            errorResponse = ErrorResponse.from(BasicErrorResult.INVALID_TOKEN);

        }
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
