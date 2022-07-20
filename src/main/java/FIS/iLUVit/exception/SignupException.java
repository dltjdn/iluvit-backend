package FIS.iLUVit.exception;


import lombok.Getter;

@Getter
public class SignupException extends RuntimeException {

    private SignupErrorResult errorResult;

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
