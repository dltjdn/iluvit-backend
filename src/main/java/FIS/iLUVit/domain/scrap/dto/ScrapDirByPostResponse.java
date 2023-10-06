package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapDirByPostResponse {
    private Long scrapId;
    private String name;
    private Boolean hasPost;

    public ScrapDirByPostResponse(Scrap scrap, Long postId) {
        scrapId = scrap.getId();
        name = scrap.getName();
        hasPost = false;
        scrap.getScrapPosts().forEach(sp->{
            if(Objects.equals(sp.getPost().getId(), postId))
                hasPost=true;
        });
    }
}
