package FIS.iLUVit.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Min(value = 0, message = "올바르지 않은 participationId 입니다")
    private Long participationId;
}
