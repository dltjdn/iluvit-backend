package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

    private UserErrorResult errorResult;

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
