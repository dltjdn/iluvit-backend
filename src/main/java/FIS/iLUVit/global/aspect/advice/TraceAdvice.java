package FIS.iLUVit.global.aspect.advice;

import FIS.iLUVit.global.aspect.trace.Trace;
import FIS.iLUVit.global.aspect.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TraceAdvice {

    private final Trace trace;

    @Around("FIS.iLUVit.global.aspect.pointcuts.TracePointcuts.allController() || " +
            "FIS.iLUVit.global.aspect.pointcuts.TracePointcuts.allRepository() || " +
            "FIS.iLUVit.global.aspect.pointcuts.TracePointcuts.allService() || " +
            "FIS.iLUVit.global.aspect.pointcuts.TracePointcuts.traceAnnotation()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status = null;

        try{
            status = trace.begin(joinPoint.getSignature().toString());
            Object result = joinPoint.proceed();
            trace.end(status);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            trace.exception(status, e);
            throw e;
        }

    }
}
