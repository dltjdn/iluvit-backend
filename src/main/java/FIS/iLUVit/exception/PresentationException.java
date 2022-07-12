package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class PresentationException extends RuntimeException {

    private PresentationErrorResult errorResult;

    public PresentationException(String message) {
        super(message);
    }

    public PresentationException() {
        super();
    }
    public PresentationException(PresentationErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
