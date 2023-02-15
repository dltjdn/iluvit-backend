package FIS.iLUVit.dto.scrap;

import FIS.iLUVit.domain.ScrapPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapPostPreviewResponse {
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
    private Long board_id;
    private String board_name;

    private LocalDate date;
    private LocalTime time;

    private Long center_id;
    private String center_name;

    private Long scrapPost_id;

    public ScrapPostPreviewResponse(ScrapPost sp) {
        this.scrapPost_id = sp.getId();
        this.post_id = sp.getPost().getId();
        this.title = sp.getPost().getTitle();
        this.content = sp.getPost().getContent();
        this.anonymous = sp.getPost().getAnonymous();
        this.nickname = sp.getPost().getUser().getNickName();
        this.user_id = sp.getPost().getUser().getId();
        this.commentCnt = sp.getPost().getCommentCnt();
        this.heartCnt = sp.getPost().getHeartCnt();
        this.imgCnt = sp.getPost().getImgCnt();
        this.board_id = sp.getPost().getBoard().getId();
        this.board_name = sp.getPost().getBoard().getName();
        this.date = sp.getPost().getDate();
        this.time = sp.getPost().getTime();

        if (sp.getPost().getBoard().getCenter() != null) {
            this.center_id = sp.getPost().getBoard().getCenter().getId();
            this.center_name = sp.getPost().getBoard().getCenter().getName();
        }
    }
}
