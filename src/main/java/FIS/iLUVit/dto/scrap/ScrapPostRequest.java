package FIS.iLUVit.dto.scrap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapPostRequest {
    private Long postId;
    private Long scrapId;
}
