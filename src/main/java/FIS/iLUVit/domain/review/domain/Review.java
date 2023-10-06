package FIS.iLUVit.domain.review.domain;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    @Lob
    private String content;             // 리뷰 내용
    private LocalDate createDate;       // 리뷰 작성 날짜
    private LocalTime createTime;       // 리뷰 작성 시간
    private LocalDate updateDate;      // 업데이트 날짜
    private LocalTime updateTime;      // 업데이트 날짜g
    private Integer score;              // 리뷰 별점 (1,2,3,4,5 점)
    private Boolean anonymous;          // 리뷰 익명 여부
    @Lob
    private String answer;              // 시설 관계자의 답글 무조건 1개
    /**
     * 작성자: 이창윤
     * 설명: 대댓글의 게시 날짜 컬럼이 없어서 추가했습니다.
     */
    private LocalDate answerCreateDate;
    private LocalTime answerCreateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;


    public static Review createReview(String content, Integer score, Boolean anonymous, Parent parent, Center center) {
        Review review = new Review();
        review.content = content;
        review.createDate = LocalDate.now();
        review.createTime = LocalTime.now();
        review.updateDate = LocalDate.now();
        review.updateTime = LocalTime.now();
        review.score = score;
        review.anonymous = anonymous;
        review.parent = parent;
        review.center = center;
        return review;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updateDate = LocalDate.now();
        this.updateTime = LocalTime.now();
    }

    public void updateAnswer(String comment, Teacher teacher) {
        this.answer = comment;
        this.answerCreateDate = LocalDate.now();
        this.answerCreateTime = LocalTime.now();
        this.teacher = teacher;
    }

    @Builder
    public Review(Long id, String content, LocalDate createDate, LocalTime createTime, LocalDate updateDate, LocalTime updateTime, Integer score, Boolean anonymous, String answer, LocalDate answerCreateDate, LocalTime answerCreateTime, Parent parent, Teacher teacher, Center center) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.createTime = createTime;
        this.updateDate = updateDate;
        this.updateTime = updateTime;
        this.score = score;
        this.anonymous = anonymous;
        this.answer = answer;
        this.answerCreateDate = answerCreateDate;
        this.answerCreateTime = answerCreateTime;
        this.parent = parent;
        this.teacher = teacher;
        this.center = center;
    }
}
