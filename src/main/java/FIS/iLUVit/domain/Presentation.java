package FIS.iLUVit.domain;

import FIS.iLUVit.dto.presentation.PresentationCreateRequest;
import FIS.iLUVit.dto.presentation.PresentationUpdateRequest;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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
            throw new PresentationException(PresentationErrorResult.CHECK_START_AND_END_DATE);

        return Presentation.builder()
                .center(center)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .place(request.getPlace())
                .build();
    }


    public void updatePresentation(PresentationUpdateRequest request) {
        if(request.getEndDate().isBefore(request.getStartDate()))
            throw new PresentationException(PresentationErrorResult.CHECK_START_AND_END_DATE);

        startDate = request.getStartDate();
        endDate = request.getEndDate();
        content = request.getContent();
        place = request.getPlace();
    }

    public void checkCanRegister() {
        LocalDate now = LocalDate.now();
        if(now.isBefore(startDate) || now.isAfter(endDate)){
            throw new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED);
        }
    }
}
