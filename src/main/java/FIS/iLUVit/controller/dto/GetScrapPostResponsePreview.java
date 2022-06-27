package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.ScrapPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetScrapPostResponsePreview {
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

    private Long scrapPost_id;

    public GetScrapPostResponsePreview(ScrapPost sp) {
        this.scrapPost_id = sp.getId();
        this.post_id = sp.getPost().getId();
        this.title = sp.getPost().getTitle();
        this.content = sp.getPost().getContent();
        this.anonymous = sp.getPost().getAnonymous();
        this.nickname = sp.getPost().getUser().getName();
        this.user_id = sp.getPost().getUser().getId();
        this.commentCnt = sp.getPost().getCommentCnt();
        this.heartCnt = sp.getPost().getHeartCnt();
        this.imgCnt = sp.getPost().getImgCnt();
        this.boardName = sp.getPost().getBoard().getName();
        this.date = sp.getPost().getDate();
        this.time = sp.getPost().getTime();

        if (sp.getPost().getBoard().getCenter() != null) {
            this.center_id = sp.getPost().getBoard().getCenter().getId();
        }
    }
}