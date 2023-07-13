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

    private Long centerId;
    private String centerName;

    private List<BoardBookmarkDto> bookmarkList = new ArrayList<>(); // 즐겨찾기한 게시판
    private List<BoardBookmarkDto> boardList = new ArrayList<>(); // 나머지 게시판

    @Getter
    @NoArgsConstructor
    public static class BoardBookmarkDto {
        private Long bookmarkId;
        private Long boardId;
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

