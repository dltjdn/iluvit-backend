package FIS.iLUVit.domain.expotoken.exception;

import lombok.Getter;

@Getter
public class ExpoTokenException extends RuntimeException{
    private ExpoTokenErrorResult errorResult;
    public ExpoTokenException(ExpoTokenErrorResult errorResult) {
        this.errorResult = errorResult;
    }
}
