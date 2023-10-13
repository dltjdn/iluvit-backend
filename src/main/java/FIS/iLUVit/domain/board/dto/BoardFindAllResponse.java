package FIS.iLUVit.domain.board.dto;

import FIS.iLUVit.domain.board.domain.Board;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access= AccessLevel.PRIVATE)
public class BoardFindAllResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;

    private List<BoardBookmarkDto> bookmarkList = new ArrayList<>(); // 즐겨찾기한 게시판
    private List<BoardBookmarkDto> boardList = new ArrayList<>(); // 나머지 게시판

    public static BoardFindAllResponse of(List<BoardBookmarkDto> bookmarkList, List<BoardBookmarkDto> boardList){
        return BoardFindAllResponse.builder()
                .centerName("모두의 이야기")
                .bookmarkList(bookmarkList)
                .boardList(boardList)
                .build();
    }

    public static BoardFindAllResponse of(Long centerId, String centerName, List<BoardBookmarkDto> bookmarkList, List<BoardBookmarkDto> boardList){
        return BoardFindAllResponse.builder()
                .centerId(centerId)
                .centerName(centerName)
                .bookmarkList(bookmarkList)
                .boardList(boardList)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class BoardBookmarkDto {
        @JsonProperty("bookmark_id")
        private Long bookmarkId;
        @JsonProperty("board_id")
        private Long boardId;
        @JsonProperty("board_name")
        private String boardName;

        public static BoardBookmarkDto from(Board board){
            return BoardBookmarkDto.builder()
                    .boardId(board.getId())
                    .boardName(board.getName())
                    .build();
        }
        public static BoardBookmarkDto of(Board board, Long bookmarkId){
            return BoardBookmarkDto.builder()
                    .bookmarkId(bookmarkId)
                    .boardId(board.getId())
                    .boardName(board.getName())
                    .build();
        }
    }

}

