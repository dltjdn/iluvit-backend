package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class updateScrapByPostRequest {
    private Long postId;
    private List<ScrapInfoForUpdate> scrapList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class ScrapInfoForUpdate {
        private Long scrapId;
        private Boolean hasPost;
    }
}