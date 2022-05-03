package FIS.iLUVit.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Post {
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

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostHeart> postHearts;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<ScrapPost> scrapPosts;
}
