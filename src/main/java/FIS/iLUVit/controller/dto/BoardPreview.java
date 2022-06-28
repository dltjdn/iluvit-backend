package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardPreview {

    private Long board_id;
    private String boardName;
    private List<PostInfo> postInfoList;
    private BoardKind boardKind;

    public BoardPreview(Long board_id, String boardName, List<PostInfo> postInfoList, BoardKind boardKind) {
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
        private Integer heartCnt;
        private Integer commentCnt;
        private List<String> images;

        public PostInfo(Post p) {
            this.post_id = p.getId();
            this.title = p.getTitle();
            this.content = p.getContent();
            this.heartCnt = p.getHeartCnt();
            this.commentCnt = p.getCommentCnt();
        }
    }

}
