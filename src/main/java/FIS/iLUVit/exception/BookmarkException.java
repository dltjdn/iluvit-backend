package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class BookmarkException extends RuntimeException {

    private BookmarkErrorResult errorResult;

    public BookmarkException() {
        super();
    }

    public BookmarkException(String message) {
        super(message);
    }

    public BookmarkException(BookmarkErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
