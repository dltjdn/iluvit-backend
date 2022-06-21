package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapListInfoResponse {

    List<ScrapInfo> data = new ArrayList<>();

    @Data
    public static class ScrapInfo {
        private Long scrapId;
        private String name;
        private Integer postsNum;

        public ScrapInfo(Scrap scrap) {
            scrapId = scrap.getId();
            name = scrap.getName();
            postsNum = scrap.getScrapPosts().size();
        }
    }
}
