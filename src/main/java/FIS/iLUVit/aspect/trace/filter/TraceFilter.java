package FIS.iLUVit.aspect.trace.filter;

import FIS.iLUVit.aspect.trace.TraceSupports;
import FIS.iLUVit.exception.exceptionHandler.SlackErrorLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TraceFilter implements Filter {

    private final SlackErrorLogger slackErrorLogger;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        TraceSupports.syncFilter(request);
        filterChain.doFilter(servletRequest, servletResponse);
        TraceSupports.releaseTraceInfos();


    }
}
