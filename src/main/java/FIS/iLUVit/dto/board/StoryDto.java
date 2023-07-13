package FIS.iLUVit.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryDto {
    private Long center_id;
    private String story_name;
    private List<BoardDto> boardDtoList;

    @Getter
    @NoArgsConstructor
    public static class BoardDto {
        private Long board_id;
        private String board_name;
        private String post_title;
        private Long post_id;

        public BoardDto(Long board_id, String board_name, String post_title, Long post_id) {
            this.board_id = board_id;
            this.board_name = board_name;
            this.post_title = post_title;
            this.post_id = post_id;
        }
    }

    public StoryDto(Long center_id, String story_name) {
        this.center_id = center_id;
        this.story_name = story_name;
    }

    public void addBoardDtoList(List<BoardDto> boardDtoList){
        this.boardDtoList = boardDtoList;
    }
}
