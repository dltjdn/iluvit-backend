package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPreviewResponse {

    private Long boardId;
    private String boardName;
    private BoardKind boardKind;
    private List<PostInfo> postInfoList;

    @Getter
    @NoArgsConstructor
    public static class PostInfo {
        private Long postId;
        private String title;
        private String content;
        private String writerNickName;
        private Boolean anonymous;
        private Integer heartCnt;
        private Integer commentCnt;
        private List<String> images;

        public PostInfo(Post post, List<String> images) {
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            if (post.getUser() != null) {
                if (post.getAnonymous()) {
                    this.writerNickName = "익명";
                } else {
                    this.writerNickName = post.getUser().getNickName();
                }
            }
            this.anonymous = post.getAnonymous();
            this.heartCnt = post.getHeartCnt();
            this.commentCnt = post.getCommentCnt();
            this.images = images;
        }
    }

}
