package FIS.iLUVit.domain.comment.exception;

import lombok.Getter;

@Getter
public class CommentException extends RuntimeException {
    private CommentErrorResult errorResult;

    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }

    public CommentException(CommentErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
