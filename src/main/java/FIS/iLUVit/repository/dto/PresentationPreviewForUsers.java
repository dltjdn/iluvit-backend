package FIS.iLUVit.repository.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationPreviewForUsers {

    private Long centerId;
    private String centerName;
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용

    @QueryProjection
    public PresentationPreviewForUsers(Presentation presentation, Center center) {
        centerId = center.getId();
        centerName = center.getName();
        presentationId = presentation.getId();
        startDate = presentation.getStartDate();
        endDate = presentation.getEndDate();
        place = presentation.getPlace();
        content = presentation.getContent();
    }
}
