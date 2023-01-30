package FIS.iLUVit.dto.waiting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitingCancelDto {
    @Min(value = 0, message = "올바르지 않은 waitingId 입니다")
    private Long waitingId;
}
