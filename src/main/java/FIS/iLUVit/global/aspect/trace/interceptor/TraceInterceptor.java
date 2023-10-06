package FIS.iLUVit.global.aspect.trace.interceptor;

import FIS.iLUVit.global.aspect.trace.TraceSupports;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class TraceInterceptor implements HandlerInterceptor {

    /**
     * trace 에 대해 세팅하기
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, Object handler) throws Exception {
        TraceSupports.syncInterceptor();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        log.info("[UUID: {}, user: {}, request: {} method: {}] time = {}ms, queries = {}",
                TraceSupports.currentUUID(),
                TraceSupports.currentUserId(),
                TraceSupports.currentURI(),
                TraceSupports.currentHttpMethod(),
                TraceSupports.time(),
                TraceSupports.currentQueryCnt()
        );
        TraceSupports.releaseTraceInfos();
    }
}
