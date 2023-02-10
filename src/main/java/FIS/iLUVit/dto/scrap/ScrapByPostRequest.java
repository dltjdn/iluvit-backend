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
public class ScrapByPostRequest {
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Long postId;
    @Valid
    private List<ScrapInfoForUpdate> scrapList = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScrapInfoForUpdate {
        @NotNull(message = "입력되지 않은 목록이 있습니다.")
        private Long scrapId;
        @NotNull(message = "입력되지 않은 목록이 있습니다.")
        private Boolean hasPost;
    }
}