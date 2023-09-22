package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class DataException extends RuntimeException {
    private DataErrorResult errorResult;
    public DataException(String message) {
        super(message);
    }

    public DataException(DataErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
