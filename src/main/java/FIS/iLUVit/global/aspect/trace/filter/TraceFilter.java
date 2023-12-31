package FIS.iLUVit.global.aspect.trace.filter;

import FIS.iLUVit.global.aspect.trace.TraceSupports;
import FIS.iLUVit.global.exception.SlackErrorLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class TraceFilter implements Filter {
    private final SlackErrorLogger slackErrorLogger;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try{
            TraceSupports.syncFilter(request);
            filterChain.doFilter(servletRequest, servletResponse);
            TraceSupports.releaseTraceInfos();
        }catch(Exception e){
            log.error("[InternalServerError] {} {} errMessage={}\n",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage()
            );
            slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
            throw e;
        }


    }
}
