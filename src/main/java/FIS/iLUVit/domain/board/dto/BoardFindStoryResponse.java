package FIS.iLUVit.domain.board.dto;

import FIS.iLUVit.domain.center.domain.Center;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class BoardFindStoryResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;
    @JsonProperty("story_name")
    private String storyName;
    private String profileImagePath;

    public static BoardFindStoryResponse from(Center center){
        BoardStoryPreviewResponseBuilder builder = BoardFindStoryResponse.builder();

        if(center == null){
            builder.storyName("모두의 이야기");
        }else{
            builder.storyName(center.getName()+"의 이야기")
                    .centerId(center.getId())
                    .centerName(center.getName())
                    .profileImagePath(center.getProfileImagePath());
        }
        return builder.build();
    }


}
