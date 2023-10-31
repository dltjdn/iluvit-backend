package FIS.iLUVit.domain.post.dto;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.board.domain.BoardKind;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostFindByBoardResponse {
    @JsonProperty("board_id")
    private Long boardId;
    private String boardName;
    private BoardKind boardKind;
    private List<PostInfo> postInfoList;

    public static PostFindByBoardResponse of(Board board, List<PostInfo> postInfoList){
        return PostFindByBoardResponse.builder()
                .boardId(board.getId())
                .boardName(board.getName())
                .boardKind(board.getBoardKind())
                .postInfoList(postInfoList)
                .build();
    }

    public static PostFindByBoardResponse hotBoardFrom(List<PostInfo> postInfoList){
        return PostFindByBoardResponse.builder()
                .boardName("HOT 게시판")
                .boardKind(BoardKind.NORMAL)
                .postInfoList(postInfoList)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class PostInfo {
        @JsonProperty("post_id")
        private Long postId;
        private String title;
        private String content;
        private String writerNickName;
        private Boolean anonymous;
        private Integer heartCnt;
        private Integer commentCnt;
        private List<String> images;

        public static PostInfo of(Post post, List<String> images){
            PostInfoBuilder builder = PostInfo.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .anonymous(post.getAnonymous())
                    .heartCnt(post.getHeartCnt())
                    .commentCnt(post.getCommentCnt())
                    .images(images);

            if (post.getUser() != null) {
                if (post.getAnonymous()) {
                    builder.writerNickName("익명");
                } else {
                    builder.writerNickName(post.getUser().getNickName());
                }
            }
            return builder.build();
        }

    }

}
