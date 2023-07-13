package FIS.iLUVit.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryDto {
    private Long centerId;
    private String storyName;
    private List<BoardDto> boardDtoList;

    @Getter
    @NoArgsConstructor
    public static class BoardDto {
        private Long boardId;
        private String boardName;
        private String postTitle;
        private Long postId;

        public BoardDto(Long board_id, String board_name, String post_title, Long post_id) {
            this.boardId = board_id;
            this.boardName = board_name;
            this.postTitle = post_title;
            this.postId = post_id;
        }
    }

}
