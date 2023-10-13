package FIS.iLUVit.domain.board.dto;

import FIS.iLUVit.domain.board.domain.Board;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardFindAllResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;

    private List<BoardBookmarkDto> bookmarkList = new ArrayList<>(); // 즐겨찾기한 게시판
    private List<BoardBookmarkDto> boardList = new ArrayList<>(); // 나머지 게시판

    @Getter
    @NoArgsConstructor
    public static class BoardBookmarkDto {
        @JsonProperty("bookmark_id")
        private Long bookmarkId;
        @JsonProperty("board_id")
        private Long boardId;
        @JsonProperty("board_name")
        private String boardName;

        public BoardBookmarkDto(Board board) {
            this.boardId = board.getId();
            this.boardName = board.getName();
        }
        public BoardBookmarkDto(Board board, Long bookmark_id) {
            this.bookmarkId = bookmark_id;
            this.boardId = board.getId();
            this.boardName = board.getName();
        }
    }

}

