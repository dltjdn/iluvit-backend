package FIS.iLUVit.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresentationForTeacherResponse {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private List<String> presentationInfoImage;
    private boolean periodValid;

    public PresentationForTeacherResponse(PresentationForTeacherDto dto,List<String> presentationInfoImage) {
        this.presentationId = dto.getPresentationId();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.place = dto.getPlace();
        this.content = dto.getContent();
        this.periodValid = !LocalDate.now().isAfter(endDate);
        this.presentationInfoImage=presentationInfoImage;
    }
}
