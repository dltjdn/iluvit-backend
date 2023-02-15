package FIS.iLUVit.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PtDateRequest {
    @Min(value = 0, message = "잘못된 설명회 회차 아이디 입니다")
    private Long ptDateId;

}
