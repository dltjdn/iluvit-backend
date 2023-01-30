package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
