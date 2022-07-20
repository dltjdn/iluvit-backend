package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

    private ErrorResult errorResult;

    // String message
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(UserErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
