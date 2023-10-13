package FIS.iLUVit.domain.board.dto;

import FIS.iLUVit.domain.center.domain.Center;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardStoryPreviewResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;
    @JsonProperty("story_name")
    private String storyName;
    private String profileImagePath;

    public BoardStoryPreviewResponse(Center center) {
        if (center == null) {
            this.storyName = "모두의 이야기";
        } else {
            this.centerId = center.getId();
            this.centerName = center.getName();
            this.storyName = center.getName() + "의 이야기";
        }
        this.profileImagePath = center.getProfileImagePath();
    }
}
