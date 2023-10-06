package FIS.iLUVit.domain.participation.dto;

import FIS.iLUVit.domain.participation.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ParticipationWithStatusResponse {
    private Status status;
    private List<ParticipationResponse> participationResponses;
}
