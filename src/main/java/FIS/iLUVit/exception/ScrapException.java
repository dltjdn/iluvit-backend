package FIS.iLUVit.exception;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class ScrapException extends RuntimeException{

    private ScrapErrorResult errorResult;

    public ScrapException() {
        super();
    }

    public ScrapException(String message) {
        super(message);
    }

    public ScrapException(ScrapErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
