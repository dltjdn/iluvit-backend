package FIS.iLUVit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationException extends RuntimeException {

    private ParticipationErrorResult participationErrorResult;


}
