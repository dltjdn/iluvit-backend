package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostResponsePreview {

    private Long post_id;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname; // 작성자 닉네임
    private Long user_id; // 작성자 id
    private int commentCnt;
    private int heartCnt;
    private int imgCnt;

    private String previewImage;
    private String boardName;

    private LocalDate date;
    private LocalTime time;

    private Long center_id;

    @QueryProjection
    public GetPostResponsePreview(Post post) {
        this.post_id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
        this.nickname = post.getUser().getName();
        this.user_id = post.getUser().getId();
        this.commentCnt = post.getCommentCnt();
        this.heartCnt = post.getHeartCnt();
        this.imgCnt = post.getImgCnt();
        this.boardName = post.getBoard().getName();
        this.date = post.getDate();
        this.time = post.getTime();
        this.previewImage = post.getInfoImagePath();

        if (post.getBoard().getCenter() != null) {
            this.center_id = post.getBoard().getCenter().getId();
        }
    }

    public GetPostResponsePreview(Post post, List<String> encodedInfoImage) {
        this.post_id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
        this.nickname = post.getUser().getName();
        this.user_id = post.getUser().getId();
        this.commentCnt = post.getCommentCnt();
        this.heartCnt = post.getHeartCnt();
        this.imgCnt = post.getImgCnt();
        this.previewImage = encodedInfoImage.isEmpty() ? null : encodedInfoImage.get(0);
        this.boardName = post.getBoard().getName();
        this.date = post.getDate();
        this.time = post.getTime();

        if (post.getBoard().getCenter() != null) {
            this.center_id = post.getBoard().getCenter().getId();
        }
    }

    public void updatePreviewImage(List<String> encodedInfoImage) {
        this.previewImage = encodedInfoImage.isEmpty() ? null : encodedInfoImage.get(0);
    }
}
