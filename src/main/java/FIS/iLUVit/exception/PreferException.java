package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;

@Getter
public class PreferException extends RuntimeException {
    private PreferErrorResult errorResult;

    public PreferException() {
        super();
    }

    public PreferException(String message) {
        super(message);
    }

    public PreferException(PreferErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
