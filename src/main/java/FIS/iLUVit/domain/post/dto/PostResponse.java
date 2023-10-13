package FIS.iLUVit.domain.post.dto;

import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    @JsonProperty("post_id")
    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname; // 작성자 닉네임

    @JsonProperty("user_id")
    private Long userId; // 작성자 id
    private String profileImage; // 작성자 프로필 이미지
    private Integer commentCnt;
    private Integer heartCnt;
    private Integer imgCnt;
    private String previewImage;
    @JsonProperty("board_name")
    private String boardName;
    @JsonProperty("board_id")
    private Long boardId;
    private LocalDate date;
    private LocalTime time;
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;

    public PostResponse(Post post, String previewImage) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
        this.nickname = post.getUser().getNickName();
        this.userId = post.getUser().getId();
        this.profileImage = post.getUser().getProfileImagePath();
        this.commentCnt = post.getCommentCnt();
        this.heartCnt = post.getHeartCnt();
        this.imgCnt = post.getImgCnt();
        this.boardName = post.getBoard().getName();
        this.boardId = post.getBoard().getId();
        this.date = post.getDate();
        this.time = post.getTime();
        this.previewImage = previewImage;

        if (post.getBoard().getCenter() != null) {
            this.centerId = post.getBoard().getCenter().getId();
            this.centerName = post.getBoard().getCenter().getName();
        } else {
            this.centerId = null;
            this.centerName = "모두의 이야기";
        }
    }
}
