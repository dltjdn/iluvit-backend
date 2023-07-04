package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseImageEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String title;                   // 개사글 제목
    @Lob
    private String content;                 // 게시글 내용
    private Boolean anonymous;              // 익명
    private LocalDate date;                 // 게시글 작성 날짜
    private LocalTime time;                 // 게시글 작성 시간
    private Integer commentCnt;             // 댓글 수
    private Integer anonymousOrder;           // 몇 번째 익명인지 기록을 위한 필드
    private Integer heartCnt;               // 좋아요 수
    private Integer videoCnt;               // 게시글 동영상 개수 최대 _개

    /**
     * BaseEntity 의 시간 정보는 추적용
     * Post 의 시간 정보 필드 끌올 기능 때문에 추가함.
     */
    private LocalDateTime postCreateDate;
    private LocalDateTime postUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostHeart> postHearts = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    private List<ScrapPost> scrapPosts = new ArrayList<>();

    @Builder(toBuilder = true)
    public Post(Long id, String title, String content, Boolean anonymous, LocalDate date, LocalTime time, Integer commentCnt, Integer heartCnt, Integer imgCnt, Integer videoCnt, LocalDateTime postCreateDate, LocalDateTime postUpdateDate, Board board, User user, List<PostHeart> postHearts, List<Comment> comments, Integer anonymousOrder) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.date = date;
        this.time = time;
        this.commentCnt = commentCnt;
        this.heartCnt = heartCnt;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.postCreateDate = postCreateDate;
        this.postUpdateDate = postUpdateDate;
        this.board = board;
        this.user = user;
        this.postHearts = postHearts;
        this.comments = comments;
        this.anonymousOrder = anonymousOrder;
    }

    public Post(String title, String content, Boolean anonymous, Integer commentCnt, Integer anonymousOrder, Integer heartCnt, Integer imgCnt, Integer videoCnt, Board board, User user) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.commentCnt = commentCnt;
        this.anonymousOrder = anonymousOrder;
        this.heartCnt = heartCnt;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.board = board;
        this.user = user;
        this.postCreateDate = LocalDateTime.now();
        this.postUpdateDate = LocalDateTime.now();
    }

    public void updatePostHeart(PostHeart postHeart) {
        this.postHearts.add(postHeart);
        this.heartCnt++;
    }

    public void updateComment(Comment comment) {
        this.comments.add(comment);
        this.commentCnt++;
    }

    public void updateTime(LocalDateTime time) {
        this.postUpdateDate = time;
    }

    public void reduceCommentCnt() {
        this.commentCnt -= 1;
    }

    public void plusAnonymousOrder() {
        this.anonymousOrder += 1;
    }
}
