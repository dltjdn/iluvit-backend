package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Board;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardListDto {

    private Long center_id;
    private String center_name;

    private List<BookmarkDTO> bookmarkList = new ArrayList<>(); // 즐겨찾기한 게시판
    private List<BookmarkDTO> boardList = new ArrayList<>(); // 나머지 게시판

    @Data
    @NoArgsConstructor
    public static class BookmarkDTO {
        private Long bookmark_id;
        private Long board_id;
        private String board_name;

        public BookmarkDTO(Board b) {
            this.board_id = b.getId();
            this.board_name = b.getName();
        }
    }

    public BoardListDto(Long center_id, String center_name) {
        this.center_id = center_id;
        this.center_name = center_name;
    }
}
