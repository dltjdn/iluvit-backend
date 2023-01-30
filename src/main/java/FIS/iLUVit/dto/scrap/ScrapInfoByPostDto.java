package FIS.iLUVit.dto.scrap;

import FIS.iLUVit.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapInfoByPostDto {
    private Long scrapId;
    private String name;
    private Boolean hasPost;

    public ScrapInfoByPostDto(Scrap scrap, Long postId) {
        scrapId = scrap.getId();
        name = scrap.getName();
        hasPost = false;
        scrap.getScrapPosts().forEach(sp->{
            if(Objects.equals(sp.getPost().getId(), postId))
                hasPost=true;
        });
    }
}
