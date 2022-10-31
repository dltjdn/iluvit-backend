package FIS.iLUVit.aspect.pointcuts;

import org.aspectj.lang.annotation.Pointcut;

public class TracePointcuts {

    @Pointcut("@annotation(FIS.iLUVit.aspect.trace.Log)")
    public void traceAnnotation(){}

    @Pointcut("execution(* FIS.iLUVit.controller..*Controller.*(..))")
    public void allController(){}

    @Pointcut("execution(* FIS.iLUVit.service..*(..))")
    public void allService(){}

    @Pointcut("execution(* FIS.iLUVit.repository..*(..))")
    public void allRepository(){}
}
