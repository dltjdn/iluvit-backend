package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class ImageException extends RuntimeException {

    private ImageErrorResult errorResult;

    public ImageException() { super(); }

    public ImageException(String message) { super(message); }

    public ImageException(ImageErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
