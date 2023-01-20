package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryHomeDto {

    private List<CenterStoryDTO> centerStoryList;

    @Data
    @NoArgsConstructor
    public static class CenterStoryDTO {
        private Long center_id;
        private String center_name;
        private String story_name;

        public CenterStoryDTO(Center center) {
            if (center == null) {
                this.story_name = "모두의 이야기";
            } else {
                this.center_id = center.getId();
                this.center_name = center.getName();
                this.story_name = center.getName() + "의 이야기";
            }
        }
    }
}
