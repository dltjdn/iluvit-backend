package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.Scrap;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ScrapDirResponse {
    private Long scrapId;
    private String name;
    private Integer postsNum;


    public static ScrapDirResponse from(Scrap scrap){
        return ScrapDirResponse.builder()
                .scrapId(scrap.getId())
                .name(scrap.getName())
                .postsNum(scrap.getScrapPosts().size())
                .build();
        }
}
