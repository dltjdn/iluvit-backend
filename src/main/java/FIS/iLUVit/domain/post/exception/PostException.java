package FIS.iLUVit.domain.post.exception;

import lombok.Getter;

@Getter
public class PostException extends RuntimeException {
    private PostErrorResult errorResult;

    public PostException() {
        super();
    }

    public PostException(String message) {
        super(message);
    }

    public PostException(PostErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
