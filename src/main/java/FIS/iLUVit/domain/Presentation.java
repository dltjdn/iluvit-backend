package FIS.iLUVit.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.CascadeType.*;

@Entity
@NoArgsConstructor
public class Presentation extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    @Lob
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개
    @ManyToOne
    @JoinColumn
    private Center center;

    @OneToMany(mappedBy = "presentation")
    @Cascade({PERSIST, REMOVE})
    private List<PtDate> ptDates = new ArrayList<>();

    @Builder
    public Presentation(Long id, LocalDate startDate, LocalDate endDate, String place, String content, Integer imgCnt, Integer videoCnt, Center center) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.content = content;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.center = center;
    }


}
