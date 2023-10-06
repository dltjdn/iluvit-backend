package FIS.iLUVit.domain.centerbookmark.exception;

import lombok.Getter;

@Getter
public class CenterBookmarkException extends RuntimeException {
    private CenterBookmarkErrorResult errorResult;

    public CenterBookmarkException() {
        super();
    }

    public CenterBookmarkException(String message) {
        super(message);
    }

    public CenterBookmarkException(CenterBookmarkErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
