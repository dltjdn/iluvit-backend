package FIS.iLUVit.exception;

public class PoliceClientException extends RuntimeException {

    private PoliceClientErrorResult errorResult;

    public PoliceClientException(PoliceClientErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}