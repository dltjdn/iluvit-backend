package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Center;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardStoryPreviewResponse {
    private Long centerId;
    private String centerName;
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
