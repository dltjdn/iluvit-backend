package FIS.iLUVit.aspect.trace;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class RequestInfo {

    private String URI;
    private HttpMethod method;

}
