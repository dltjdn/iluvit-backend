package FIS.iLUVit.aspect.trace;

public interface Trace {

    TraceStatus begin(String message);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);
}
