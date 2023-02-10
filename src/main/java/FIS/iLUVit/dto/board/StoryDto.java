package FIS.iLUVit.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
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

        public BoardDto(Long boardId, String boardName, String postTitle, Long postId) {
            this.boardId = boardId;
            this.boardName = boardName;
            this.postTitle = postTitle;
            this.postId = postId;
        }
    }

    public StoryDto(Long centerId, String storyName) {
        this.centerId = centerId;
        this.storyName = storyName;
    }

    public void addBoardDtoList(List<BoardDto> boardDtoList){
        this.boardDtoList = boardDtoList;
    }
}
