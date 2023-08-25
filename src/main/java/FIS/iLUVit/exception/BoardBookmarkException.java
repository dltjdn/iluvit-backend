package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class BoardBookmarkException extends RuntimeException {

    private BoardBookmarkErrorResult errorResult;

    public BoardBookmarkException() {
        super();
    }

    public BoardBookmarkException(String message) {
        super(message);
    }

    public BoardBookmarkException(BoardBookmarkErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
