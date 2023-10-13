package FIS.iLUVit.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardStoryResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("story_name")
    private String storyName;
    private List<BoardDto> boardDtoList;

    @Getter
    @NoArgsConstructor
    public static class BoardDto {
        @JsonProperty("board_id")
        private Long boardId;
        @JsonProperty("board_name")
        private String boardName;
        @JsonProperty("post_title")
        private String postTitle;
        @JsonProperty("post_id")
        private Long postId;

        public BoardDto(Long board_id, String board_name, String post_title, Long post_id) {
            this.boardId = board_id;
            this.boardName = board_name;
            this.postTitle = post_title;
            this.postId = post_id;
        }
    }

}
