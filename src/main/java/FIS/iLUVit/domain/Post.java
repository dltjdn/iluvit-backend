package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String title;                   // 개사글 제목
    @Lob
    private String content;                 // 게시글 내용
    private Boolean anonymous;              // 익명
    private LocalDate date;                 // 게시글 작성 날짜
    private LocalTime time;                 // 게시글 작성 시간
    private Integer commentCnt;             //댓글 수
    private Integer heartCnt;               //좋아요 수
    private Integer imgCnt;                 // 게시글 이미지 개수 최대 __장
    private Integer videoCnt;               // 게시글 동영상 개수 최대 _개

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostHeart> postHearts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<ScrapPost> scrapPosts = new ArrayList<>();

    public Post(String title, String content, Boolean anonymous, Integer commentCnt, Integer heartCnt, Integer imgCnt, Integer videoCnt, Board board, User user) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.commentCnt = commentCnt;
        this.heartCnt = heartCnt;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.board = board;
        this.user = user;
    }

    public void updatePostHeart(PostHeart postHeart) {
        this.postHearts.add(postHeart);
        this.heartCnt++;
    }

    public void updateComment(Comment comment) {
        this.comments.add(comment);
        this.commentCnt++;
    }
}
