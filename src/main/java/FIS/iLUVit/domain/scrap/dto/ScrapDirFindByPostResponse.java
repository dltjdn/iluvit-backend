package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.Scrap;
import lombok.*;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ScrapDirFindByPostResponse {
    private Long scrapId;
    private String name;
    private Boolean hasPost;

    public static ScrapDirFindByPostResponse of(Scrap scrap, boolean hasPost){
        return ScrapDirFindByPostResponse.builder()
                .scrapId(scrap.getId())
                .name(scrap.getName())
                .hasPost(hasPost)
                .build();
    }


}
