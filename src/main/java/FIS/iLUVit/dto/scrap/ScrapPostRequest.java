package FIS.iLUVit.dto.scrap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapPostRequest {
    private Long postId;
    private Long scrapId;
}
