package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Min(value = 0, message = "올바르지 않은 participationId 입니다")
    private Long participationId;
}
