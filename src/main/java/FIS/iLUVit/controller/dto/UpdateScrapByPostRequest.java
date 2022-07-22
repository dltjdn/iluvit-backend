package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateScrapByPostRequest {
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Long postId;
    @Valid
    private List<ScrapInfoForUpdate> scrapList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScrapInfoForUpdate {
        @NotNull(message = "입력되지 않은 목록이 있습니다.")
        private Long scrapId;
        @NotNull(message = "입력되지 않은 목록이 있습니다.")
        private Boolean hasPost;
    }
}