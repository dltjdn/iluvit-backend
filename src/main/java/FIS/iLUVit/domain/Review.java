package FIS.iLUVit.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Review{
    @Id @GeneratedValue
    private Long id;
    @Lob
    private String content;             // 리뷰 내용
    private LocalDate createDate;       // 리뷰 작성 날짜
    private LocalTime createTime;       // 리뷰 작성 시간
    private LocalDate updatedDate;      // 업데이트 날짜
    private LocalTime updatedTime;      // 업데이트 날짜
    private Integer score;              // 리뷰 별점 (1,2,3,4,5 점)
    private Boolean anonymous;          // 리뷰 익명 여부
    @Lob
    private String answer;              // 시설 관계자의 답글 무조건 1개

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "review")
    private List<ReviewHeart> reviewHearts;
}
