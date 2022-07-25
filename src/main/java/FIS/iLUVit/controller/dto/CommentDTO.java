package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private LocalDate date;          // 게시글 작성 날짜
    private LocalTime time;          // 게시글 작성 시간
    private Boolean anonymous;       // 익명 댓글 여부
    private String content;

    private Long post_id; // 연관된 게시글 아이디
    private String post_title;
    private Integer post_heart_cnt;
    private Integer post_comment_cnt;
    private Long user_id; // 댓글 작성자 아이디
    private Long board_id; // 게시판 아이디
    private String board_name; // 게시판 이름
    private Long center_id; // 시설 아이디
    private String center_name; // 시설명

    public CommentDTO(Comment c) {
        this.id = c.getId();
        this.date = c.getDate();
        this.time = c.getTime();
        this.anonymous = c.getAnonymous();
        this.content = c.getContent();
        this.post_id = c.getPost().getId();
        this.post_title = c.getPost().getTitle();
        this.post_heart_cnt = c.getPost().getHeartCnt();
        this.post_comment_cnt = c.getPost().getCommentCnt();
        this.user_id = c.getUser().getId();
        this.board_id = c.getPost().getBoard().getId();
        this.board_name = c.getPost().getBoard().getName();
        this.center_id = c.getPost().getBoard().getCenter().getId();
        this.center_name = c.getPost().getBoard().getCenter().getName();
    }
}
