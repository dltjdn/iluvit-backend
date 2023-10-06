package FIS.iLUVit.domain.review.exception;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {
    private ReviewErrorResult errorResult;

    public ReviewException() {
        super();
    }

    public ReviewException(String message) {
        super(message);
    }

    public ReviewException(ReviewErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
