package FIS.iLUVit.domain.child.exception;

import lombok.Getter;

@Getter
public class ChildException extends RuntimeException{
    private ChildErrorResult errorResult;
    public ChildException(ChildErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
