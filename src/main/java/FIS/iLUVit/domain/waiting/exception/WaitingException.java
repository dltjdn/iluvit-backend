package FIS.iLUVit.domain.waiting.exception;

import lombok.Getter;

@Getter
public class WaitingException extends RuntimeException {

    WaitingErrorResult ErrorResult;

    public WaitingException(WaitingErrorResult ex) {
        super();
        ErrorResult = ex;
    }
}
