package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class WaitingRegisterDto {
    @Min(value = 0, message = "올바르지 않은 ptDateId 입니다")
    private Long ptDateId;
}
