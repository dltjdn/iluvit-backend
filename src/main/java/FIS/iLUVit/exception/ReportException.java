package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {
    private ReportErrorResult errorResult;

    public ReportException() {
        super();
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(ReportErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
