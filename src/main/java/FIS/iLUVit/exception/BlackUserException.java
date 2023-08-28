package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class BlackUserException extends RuntimeException {

    private BlackUserResult errorResult;

    public BlackUserException() { super(); }

    public BlackUserException(String message) { super(message); }

    public BlackUserException(BlackUserResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}