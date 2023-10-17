package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.ScrapPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostByScrapDirResponse {
    @JsonProperty("post_id")
    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname;    // 작성자 닉네임
    @JsonProperty("user_id")
    private Long userId;        // 작성자 id
    private int commentCnt;
    private int heartCnt;
    private int imgCnt;

    private String previewImage;
    @JsonProperty("board_id")
    private Long boardId;
    @JsonProperty("board_name")
    private String boardName;

    private LocalDate date;
    private LocalTime time;
    @JsonProperty("center_id")

    private Long centerId;

    @JsonProperty("center_name")
    private String centerName;

    @JsonProperty("scrapPost_id")
    private Long scrapPostId;

    public PostByScrapDirResponse(ScrapPost sp) {
        this.scrapPostId = sp.getId();
        this.postId = sp.getPost().getId();
        this.title = sp.getPost().getTitle();
        this.content = sp.getPost().getContent();
        this.anonymous = sp.getPost().getAnonymous();
        this.nickname = sp.getPost().getUser().getNickName();
        this.userId = sp.getPost().getUser().getId();
        this.commentCnt = sp.getPost().getCommentCnt();
        this.heartCnt = sp.getPost().getHeartCnt();
        this.imgCnt = sp.getPost().getImgCnt();
        this.boardId = sp.getPost().getBoard().getId();
        this.boardName = sp.getPost().getBoard().getName();
        this.date = sp.getPost().getDate();
        this.time = sp.getPost().getTime();

        if (sp.getPost().getBoard().getCenter() != null) {
            this.centerId = sp.getPost().getBoard().getCenter().getId();
            this.centerName = sp.getPost().getBoard().getCenter().getName();
        }
    }
}