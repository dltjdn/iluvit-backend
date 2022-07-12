package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class CenterException extends RuntimeException{

    private CenterErrorResult errorResult;

    public CenterException(String message) {
        super(message);
    }

    public CenterException(CenterErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
