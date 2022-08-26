package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckLoginIdRequest {
    @Size(min = 5, message = "아이디는 5자 이상이어야 합니다.")
    private String loginId;
}
