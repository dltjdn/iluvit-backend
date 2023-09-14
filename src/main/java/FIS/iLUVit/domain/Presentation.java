package FIS.iLUVit.domain;

import FIS.iLUVit.dto.presentation.PresentationCreateRequest;
import FIS.iLUVit.dto.presentation.PresentationRequest;
import FIS.iLUVit.exception.PresentationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.CascadeType.*;

@Entity
@NoArgsConstructor
@Getter
public class Presentation extends BaseImageEntity {
    @Id @GeneratedValue
    private Long id;

    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    @Lob
    private String content;             // 설명회 내용
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Center center;

    @OneToMany(mappedBy = "presentation")
    @Cascade({PERSIST, REMOVE})
    private List<PtDate> ptDates = new ArrayList<>();


    @Builder
    public Presentation(LocalDate startDate, LocalDate endDate, String place, String content, Integer imgCnt, Integer videoCnt, Center center) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.content = content;
        this.imgCnt = imgCnt;
        this.videoCnt = videoCnt;
        this.center = center;
    }

    public static Presentation createPresentation(PresentationCreateRequest request, Center center){
        if(request.getEndDate().isBefore(request.getStartDate()))
            throw new PresentationException("시작일자와 종료일자를 다시 확인해 주세요.");

        return Presentation.builder()
                .center(center)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .place(request.getPlace())
                .build();
    }


    public Presentation update(PresentationRequest request) {
        startDate = request.getStartDate();
        endDate = request.getEndDate();
        if(endDate.isBefore(startDate))
            throw new PresentationException("시작일자와 종료일자를 다시 확인해 주세요.");
        content = request.getContent();
        place = request.getPlace();
        return this;
    }

    public void canRegister() {
        LocalDate now = LocalDate.now();
        if(now.isBefore(startDate) || now.isAfter(endDate)){
            throw new PresentationException("신청기간이 지났습니다");
        }
    }
}
