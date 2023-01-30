package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardPreviewDto {

    private Long board_id;
    private String boardName;
    private List<PostInfo> postInfoList;
    private BoardKind boardKind;

    public BoardPreviewDto(Long board_id, String boardName, List<PostInfo> postInfoList, BoardKind boardKind) {
        this.board_id = board_id;
        this.boardName = boardName;
        this.postInfoList = postInfoList;
        this.boardKind = boardKind;
    }

    @Data
    @NoArgsConstructor
    public static class PostInfo {

        private Long post_id;
        private String title;
        private String content;
        private String writerNickName;
        private Boolean anonymous;
        private Integer heartCnt;
        private Integer commentCnt;
        private List<String> images = new ArrayList<>();

        public PostInfo(Post p) {
            this.post_id = p.getId();
            this.title = p.getTitle();
            this.content = p.getContent();
            if (p.getUser() != null) {
                if (p.getAnonymous()) {
                    this.writerNickName = "익명";
                } else {
                    this.writerNickName = p.getUser().getNickName();
                }
            }
            this.anonymous = p.getAnonymous();
            this.heartCnt = p.getHeartCnt();
            this.commentCnt = p.getCommentCnt();
        }
    }

}
