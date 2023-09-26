package FIS.iLUVit.exception;

public class ChildException extends RuntimeException{
    private ChildErrorResult errorResult;
    public ChildException(ChildErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
