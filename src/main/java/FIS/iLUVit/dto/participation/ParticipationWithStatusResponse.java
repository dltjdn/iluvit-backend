package FIS.iLUVit.dto.participation;

import FIS.iLUVit.domain.enumtype.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ParticipationWithStatusResponse {
    private Status status;
    private List<ParticipationResponse> participationResponses;
}
