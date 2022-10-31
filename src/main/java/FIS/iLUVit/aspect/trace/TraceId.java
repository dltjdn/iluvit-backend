package FIS.iLUVit.aspect.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TraceId {

    private String UUID;
    private Long userId;
    private String request;
    private String method;
    private int level;

    private TraceId(String UUID, Long userId, String request, String method) {
        this.UUID = UUID;
        this.userId = userId;
        this.request = request;
        this.method = method;
        this.level = 1;
    }

    public static TraceId createTraceId(String UUID, Long userId, String request, String method){
        return new TraceId(UUID, userId, request, method);
    }

    public boolean isFirstLevel(){
        return level == 1;
    }

    public TraceId levelUp(){
        level++;
        return this;
    }

    public TraceId levelDown(){
        level--;
        return this;
    }

    @Override
    public String toString() {
        return "requestId : " + UUID + " userId : " + userId + " 요청내용 : " + request;
    }

    public TraceId createNextId() {
        return new TraceId(UUID, userId, request, method,level + 1);
    }

    public TraceId createPreviousId() {
        return new TraceId(UUID, userId, request, method,level - 1);
    }

}
