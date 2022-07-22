package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddScrapRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String name;
}
