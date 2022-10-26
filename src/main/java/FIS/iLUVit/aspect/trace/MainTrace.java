package FIS.iLUVit.aspect.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static FIS.iLUVit.aspect.trace.TraceId.createTraceId;

@Component
@Slf4j
public class MainTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<TraceId> traceHolder = new ThreadLocal<>();

    // userId 를 홀딩 security 에서 진행
    private ThreadLocal<Long> userHolder = new ThreadLocal<>();
    // request 정보 담기 위해 사용 별도의 interceptor 활용 or security 에서... interceptor 로 따로 빼는 것이 좋을 듯...
    private ThreadLocal<RequestInfo> requestHolder = new ThreadLocal<>();

    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[UUID: {}, user: {}, request: {} method: {}] {} {}",
                traceId.getUUID(),
                traceId.getUserId(),
                traceId.getRequest(),
                traceId.getMethod(),
                addSpace(START_PREFIX, traceId.getLevel()),
                message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    public void end(TraceStatus status) {
        complete(status, null);
    }

    /**
     * 로그 예외 처리
     */
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e){
        Long stopTimeMs = System.currentTimeMillis();

        // 종료시 까지 걸린 시간
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = traceHolder.get();
        if(e == null){
            log.info("[UUID: {}, user: {}, request: {} method: {}] {}{} time = {}ms",
                    traceId.getUUID(),
                    traceId.getUserId(),
                    traceId.getRequest(),
                    traceId.getMethod(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    status.getMessage(), resultTimeMs);
        } else {
            log.info("[UUID: {}, user: {}, request: {} method: {}] {}{} time = {}ms",
                    traceId.getUUID(),
                    traceId.getUserId(),
                    traceId.getRequest(),
                    traceId.getMethod(),
                    addSpace(EX_PREFIX, traceId.getLevel()),
                    status.getMessage(), resultTimeMs);
        }
        releaseTraceId();
    }

    private void syncTraceId(){
        TraceId traceId = traceHolder.get();
        Long userId = userHolder.get();
        if(userId == null){
            userId = -1L;
            userHolder.set(userId);
        }
        RequestInfo requestInfo = requestHolder.get();

        if(traceId == null){
            traceHolder.set(createTraceId(userId, requestInfo.getURI(), requestInfo.getMethod().toString()));
        } else {
            traceId.levelUp();
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceHolder.get();
        if (traceId.isFirstLevel()) {
            traceHolder.remove();
            userHolder.remove();
            requestHolder.remove();
        } else {
            traceHolder.get().levelDown();
        }
    }

    /**
     * level 에 따라서 prefix 의 위치를 변경 할 수 있다.
     */
    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}
