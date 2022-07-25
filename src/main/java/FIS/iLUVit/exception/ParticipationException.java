package FIS.iLUVit.exception;

import lombok.Getter;

@Getter
public class ParticipationException extends RuntimeException {

    private ParticipationErrorResult errorResult;

    public ParticipationException(ParticipationErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
