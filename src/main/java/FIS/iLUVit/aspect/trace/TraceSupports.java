package FIS.iLUVit.aspect.trace;

import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
public class TraceSupports {

    private static final ThreadLocal<String> UUID_Holder = new ThreadLocal<>();
    // userId 를 홀딩 security 에서 진행
    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();
    // request 정보 담기 위해 사용 별도의 interceptor 활용 or security 에서... interceptor 로 따로 빼는 것이 좋을 듯...
    private static final ThreadLocal<RequestInfo> requestHolder = new ThreadLocal<>();
    // query 수 측정을 위한 카운터
    private static final ThreadLocal<Long> queryCountHolder = new ThreadLocal<>();
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public static void syncFilter(HttpServletRequest request){
        syncUUID();
        syncRequest(request);
        syncQueryCount();
        syncStartTime();
    }

    public static void syncInterceptor(){
        syncUser();
    }

    public static void releaseTraceInfos(){
        userHolder.remove();
        requestHolder.remove();
        queryCountHolder.remove();
        UUID_Holder.remove();
        startTime.remove();
    }

    public static Long time(){
        return System.currentTimeMillis() - startTime.get();
    }

    private static void syncUUID(){
        UUID_Holder.set(UUID.randomUUID().toString().substring(0, 17));
    }

    private static void syncUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if(principal instanceof PrincipalDetails){
            Long userId = ((PrincipalDetails) principal).getUser().getId();
            userHolder.set(userId);
        } else {
            userHolder.set(-1L);
        }
    }

    private static void syncRequest(HttpServletRequest request){
        String URI = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        requestHolder.set(new RequestInfo(URI, method));
    }

    private static void syncQueryCount(){
        queryCountHolder.set(0L);
    }

    private static void syncStartTime(){
        startTime.set(System.currentTimeMillis());
    }

    public static Long currentUserId(){
        return userHolder.get();
    }

    public static String currentURI(){
        if(requestHolder.get() == null){
            return null;
        }
        return requestHolder.get().getURI();
    }

    public static HttpMethod currentHttpMethod(){
        if(requestHolder.get() == null){
            return HttpMethod.TRACE;
        }
        return requestHolder.get().getMethod();
    }

    public static String currentUUID(){
        return UUID_Holder.get();
    }

    public static Long currentQueryCnt(){
        return queryCountHolder.get();
    }

    public static void countQuery(){
        if(queryCountHolder.get() == null){
            queryCountHolder.set(0L);
        }
        queryCountHolder.set(queryCountHolder.get() + 1);
    }
}
