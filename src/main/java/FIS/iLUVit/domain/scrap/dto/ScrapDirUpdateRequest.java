package FIS.iLUVit.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ScrapDirUpdateRequest {
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Long scrapId;
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Boolean hasPost;
}
