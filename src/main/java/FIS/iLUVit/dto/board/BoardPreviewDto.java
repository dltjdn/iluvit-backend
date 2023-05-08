package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPreviewDto {

    private Long board_id;
    private String boardName;
    private List<PostInfo> postInfoList;
    private BoardKind boardKind;

    @Getter
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

        public PostInfo(Post post) {
            this.post_id = post.getId();
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
        }

        public void addImagesInPostInfo(List<String> images){
            this.images= images;
        }
    }

}
