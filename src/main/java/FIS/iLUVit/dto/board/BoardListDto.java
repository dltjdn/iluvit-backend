package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {

    private Long center_id;
    private String center_name;

    private List<BoardBookmarkDto> bookmarkList = new ArrayList<>(); // 즐겨찾기한 게시판
    private List<BoardBookmarkDto> boardList = new ArrayList<>(); // 나머지 게시판

    @Getter
    @NoArgsConstructor
    public static class BoardBookmarkDto {
        private Long bookmark_id;
        private Long board_id;
        private String board_name;

        public BoardBookmarkDto(Board board) {
            this.board_id = board.getId();
            this.board_name = board.getName();
        }
        public BoardBookmarkDto(Board board, Long bookmark_id) {
            this.bookmark_id = bookmark_id;
            this.board_id = board.getId();
            this.board_name = board.getName();
        }
    }

}

