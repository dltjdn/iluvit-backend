package FIS.iLUVit.domain.post.domain;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.common.domain.BaseImageEntity;
import FIS.iLUVit.domain.user.domain.User;
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
    private List<Comment> comments = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    public Post( String title, String content, Boolean anonymous, LocalDate date, LocalTime time, Integer commentCnt, Integer heartCnt, Integer imgCnt, Integer videoCnt, LocalDateTime postCreateDate, LocalDateTime postUpdateDate, Board board, User user, List<Comment> comments, Integer anonymousOrder) {
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
        this.comments = comments;
        this.anonymousOrder = anonymousOrder;
    }

    public static Post of(String title, String content, Boolean anonymous,Integer imgCnt, Board board, User user){
        return Post.builder()
                .title(title)
                .content(content)
                .anonymous(anonymous)
                .commentCnt(0)
                .anonymousOrder(0)
                .heartCnt(0)
                .imgCnt(imgCnt)
                .videoCnt(0)
                .board(board)
                .user(user)
                .build();
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

    public void plusHeartCount() { this.heartCnt += 1; }
}