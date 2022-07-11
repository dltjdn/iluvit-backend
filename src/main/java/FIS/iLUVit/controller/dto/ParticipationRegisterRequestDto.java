package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRegisterRequestDto {
    @Min(value = 0, message = "잘못된 설명회 회차 아이디 입니다")
    private Long ptDateId;

}
