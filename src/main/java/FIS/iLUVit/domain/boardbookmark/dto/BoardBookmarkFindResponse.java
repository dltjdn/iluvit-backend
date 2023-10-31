package FIS.iLUVit.domain.boardbookmark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class BoardBookmarkFindResponse {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("story_name")
    private String storyName;
    private List<BoardDto> boardDtoList;

    public static BoardBookmarkFindResponse of(Long centerId, String storyName, List<BoardDto> boardDtoList){
        return BoardBookmarkFindResponse.builder()
                .centerId(centerId)
                .storyName(storyName)
                .boardDtoList(boardDtoList)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    public static class BoardDto {
        @JsonProperty("board_id")
        private Long boardId;
        @JsonProperty("board_name")
        private String boardName;
        @JsonProperty("post_title")
        private String postTitle;
        @JsonProperty("post_id")
        private Long postId;

        public static BoardDto of(Long boardId, String boardName, String postTitle, Long postId){
            return BoardDto.builder()
                    .boardId(boardId)
                    .boardName(boardName)
                    .postTitle(postTitle)
                    .postId(postId)
                    .build();
        }
    }

}
