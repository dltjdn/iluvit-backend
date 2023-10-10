package FIS.iLUVit.domain.participation.dto;

import FIS.iLUVit.domain.participation.domain.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ParticipationWithStatusResponse {
    private Map<Status, List<ParticipationResponse>> participationWithStatus;

    public static ParticipationWithStatusResponse from( Map<Status, List<ParticipationResponse>> participationWithStatus){
        return ParticipationWithStatusResponse.builder()
                .participationWithStatus(participationWithStatus)
                .build();
    }
}
