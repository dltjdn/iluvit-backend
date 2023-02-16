package FIS.iLUVit.dto.scrap;

import FIS.iLUVit.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapInfoDto {

        private Long scrapId;
        private String name;
        private Integer postsNum;

        public ScrapInfoDto(Scrap scrap) {
            scrapId = scrap.getId();
            name = scrap.getName();
            postsNum = scrap.getScrapPosts().size();
        }
}
