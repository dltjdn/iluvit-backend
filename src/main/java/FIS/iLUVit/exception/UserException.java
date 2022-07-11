package FIS.iLUVit.exception;

public class UserException extends RuntimeException{

    // String message
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }
}
