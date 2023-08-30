package FIS.iLUVit.dto.comment;

import FIS.iLUVit.domain.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CommentPostResponse {
    private Long id;
    private LocalDate date;          // 게시글 작성 날짜
    private LocalTime time;          // 게시글 작성 시간
    private Boolean anonymous;       // 익명 댓글 여부
    private String content;
    private Long postId; // 연관된 게시글 아이디
    private String postTitle;
    private Integer heartCnt; // 게시글 좋아요 수
    private Integer commentCnt; // 게시글 댓글 수
    private Long userId; // 댓글 작성자 아이디
    private Long boardId; // 게시판 아이디
    private String boardName; // 게시판 이름
    private Long centerId; // 시설 아이디
    private String centerName; // 시설명

    public CommentPostResponse(Comment c) {
        this.id = c.getId();
        this.date = c.getDate();
        this.time = c.getTime();
        this.anonymous = c.getAnonymous();
        this.content = c.getContent();
        this.postId = c.getPost().getId();
        this.postTitle = c.getPost().getTitle();
        this.heartCnt = c.getPost().getHeartCnt();
        this.commentCnt = c.getPost().getCommentCnt();
        this.userId = c.getUser().getId();
        this.boardId = c.getPost().getBoard().getId();
        this.boardName = c.getPost().getBoard().getName();
        if (c.getPost().getBoard().getCenter() != null) {
            this.centerId = c.getPost().getBoard().getCenter().getId();
            this.centerName = c.getPost().getBoard().getCenter().getName();
        } else {
            this.centerName = "모두의 이야기";
        }
    }
}
