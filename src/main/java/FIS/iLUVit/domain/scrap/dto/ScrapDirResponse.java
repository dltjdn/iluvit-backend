package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapDirResponse {
    private Long scrapId;
    private String name;
    private Integer postsNum;

    public ScrapDirResponse(Scrap scrap) {
            scrapId = scrap.getId();
            name = scrap.getName();
            postsNum = scrap.getScrapPosts().size();
        }
}
