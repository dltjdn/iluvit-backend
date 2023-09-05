package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class BlockedException extends RuntimeException {
    private BlockedErrorResult errorResult;

    public BlockedException() { super(); }

    public BlockedException(String message) { super(message); }

    public BlockedException(BlockedErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}