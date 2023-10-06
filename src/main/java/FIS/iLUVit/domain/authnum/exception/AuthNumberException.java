package FIS.iLUVit.domain.authnum.exception;

import lombok.Getter;

@Getter
public class AuthNumberException extends RuntimeException {

    private AuthNumberErrorResult errorResult;

    public AuthNumberException() {
        super();
    }

    public AuthNumberException(String message) {
        super(message);
    }

    public AuthNumberException(AuthNumberErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }

}
