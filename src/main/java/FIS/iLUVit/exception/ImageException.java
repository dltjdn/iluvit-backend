package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class ImageException extends RuntimeException {

    private final ImageErrorResult imageExceptionErrorResult;

    public ImageException(ImageErrorResult imageExceptionErrorResult) {
        super();
        this.imageExceptionErrorResult = imageExceptionErrorResult;
    }
}
