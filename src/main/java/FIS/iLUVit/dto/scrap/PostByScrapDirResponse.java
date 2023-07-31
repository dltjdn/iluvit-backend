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
public class PostByScrapDirResponse {
    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname;    // 작성자 닉네임
    private Long userId;        // 작성자 id
    private int commentCnt;
    private int heartCnt;
    private int imgCnt;

    private String previewImage;
    private Long boardId;
    private String boardName;

    private LocalDate date;
    private LocalTime time;

    private Long centerId;
    private String centerName;

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