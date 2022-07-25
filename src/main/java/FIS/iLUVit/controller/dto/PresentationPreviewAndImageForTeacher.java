package FIS.iLUVit.controller.dto;

import FIS.iLUVit.repository.dto.PresentationPreviewForTeacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationPreviewAndImageForTeacher {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private List<String> presentationInfoImage;
    private boolean periodValid;

    public PresentationPreviewAndImageForTeacher(PresentationPreviewForTeacher dto) {
        this.presentationId = dto.getPresentationId();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.place = dto.getPlace();
        this.content = dto.getContent();
        periodValid = !LocalDate.now().isAfter(endDate);
    }
}
