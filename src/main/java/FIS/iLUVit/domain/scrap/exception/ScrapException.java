package FIS.iLUVit.domain.scrap.exception;

import lombok.Getter;

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
