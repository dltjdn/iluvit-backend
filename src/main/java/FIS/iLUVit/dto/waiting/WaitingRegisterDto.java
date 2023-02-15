package FIS.iLUVit.dto.waiting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.Min;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class WaitingRegisterDto {
    @Min(value = 0, message = "올바르지 않은 ptDateId 입니다")
    private Long ptDateId;
}
