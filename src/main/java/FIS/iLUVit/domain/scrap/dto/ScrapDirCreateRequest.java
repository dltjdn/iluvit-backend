package FIS.iLUVit.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class ScrapDirCreateRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String name;
}
