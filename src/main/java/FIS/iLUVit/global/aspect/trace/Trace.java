package FIS.iLUVit.global.aspect.trace;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static FIS.iLUVit.global.aspect.trace.TraceId.createTraceId;
import static FIS.iLUVit.global.aspect.trace.TraceSupports.*;

@Component
@Slf4j
@Getter
public class Trace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private final ThreadLocal<TraceId> traceHolder = new ThreadLocal<>();

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
                    status.getMessage(),
                    resultTimeMs
                    );
        } else {
            log.info("[UUID: {}, user: {}, request: {} method: {}] {}{} time = {}ms" +
                    traceId.getUUID(),
                    traceId.getUserId(),
                    traceId.getRequest(),
                    traceId.getMethod(),
                    addSpace(EX_PREFIX, traceId.getLevel()),
                    status.getMessage(),
                    resultTimeMs
            );
        }
        releaseTraceId();
    }

    private void syncTraceId(){
        TraceId traceId = traceHolder.get();
        Long userId = currentUserId();

        if(traceId == null){
            traceHolder.set(createTraceId(currentUUID(), userId, currentURI(), currentHttpMethod().toString()));
        } else {
            traceId.levelUp();
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceHolder.get();
        if (traceId.isFirstLevel()) {
            traceHolder.remove();
        } else {
            traceId.levelDown();
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
