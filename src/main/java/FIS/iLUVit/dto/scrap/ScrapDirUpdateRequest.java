package FIS.iLUVit.dto.scrap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapDirUpdateRequest {
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Long scrapId;
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Boolean hasPost;
}
