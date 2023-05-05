package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.common.Center;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryPreviewDto {
    private Long center_id;
    private String center_name;
    private String story_name;

    public StoryPreviewDto(Center center) {
        if (center == null) {
            this.story_name = "모두의 이야기";
        } else {
            this.center_id = center.getId();
            this.center_name = center.getName();
            this.story_name = center.getName() + "의 이야기";
        }
    }
}
