package FIS.iLUVit.exception;


import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;

@Getter
public class SignupException extends RuntimeException {

    private ErrorResult errorResult;

    public SignupException() {
        super();
    }

    public SignupException(String message) {
        super(message);
    }

    public SignupException(SignupErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
