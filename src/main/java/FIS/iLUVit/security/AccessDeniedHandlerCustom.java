package FIS.iLUVit.security;

import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AccessDeniedHandlerCustom implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("[AccessDeniedHandlerCustom] Forbidden error : {}", accessDeniedException.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
