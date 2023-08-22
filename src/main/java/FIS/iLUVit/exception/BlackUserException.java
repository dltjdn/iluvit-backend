package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class BlackUserException extends RuntimeException {

    private BlackUserErrorResult errorResult;

    public BlackUserException() { super(); }

    public BlackUserException(String message) { super(message); }

    public BlackUserException(BlackUserErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
