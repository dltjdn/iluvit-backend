package FIS.iLUVit.aspect.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public class RequestInfo {

    private String URI;
    private HttpMethod method;

}
