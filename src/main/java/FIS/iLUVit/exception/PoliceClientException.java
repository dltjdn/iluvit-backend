package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class PoliceClientException extends RuntimeException {

    private PoliceClientErrorResult errorResult;

    public PoliceClientException(PoliceClientErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}