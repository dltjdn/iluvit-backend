package FIS.iLUVit.domain.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresentationForTeacherDto {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private String presentationInfoImage;
    private boolean periodValid;

    public PresentationForTeacherDto(Long presentationId, LocalDate startDate, LocalDate endDate, String place, String content, String presentationInfoImage) {
        this.presentationId = presentationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.content = content;
        this.presentationInfoImage = presentationInfoImage;
        periodValid = !LocalDate.now().isAfter(endDate);
    }
}
