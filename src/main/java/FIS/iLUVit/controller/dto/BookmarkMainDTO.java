package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BookmarkMainDTO {

    private List<StoryDTO> stories = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class StoryDTO {
        private Long center_id;
        private String story_name;
        private List<BoardDTO> boardDTOList;

        public StoryDTO(Long center_id, String story_name) {
            this.center_id = center_id;
            this.story_name = story_name;
        }
    }

    @Data
    @NoArgsConstructor
    public static class BoardDTO {
        private Long board_id;
        private String board_name;
        private String post_title;
        private Long post_id;

        public BoardDTO(Long board_id, String board_name, String post_title, Long post_id) {
            this.board_id = board_id;
            this.board_name = board_name;
            this.post_title = post_title;
            this.post_id = post_id;
        }
    }
}
