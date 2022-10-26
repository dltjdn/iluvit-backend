package FIS.iLUVit.aspect.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.UUID.randomUUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TraceId {

    private String UUID;
    private Long userId;
    private String request;
    private String method;
    private int level;

    public TraceId(Long userId, String request, String method) {
        UUID = String.valueOf(randomUUID());
        this.userId = userId;
        this.request = request;
        this.method = method;
        this.level = 0;
    }

    public static TraceId createTraceId(Long userId, String request, String method){
        return new TraceId(userId, request, method);
    }

    public boolean isFirstLevel(){
        return level == 0;
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
