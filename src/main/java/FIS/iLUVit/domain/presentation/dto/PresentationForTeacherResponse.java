package FIS.iLUVit.domain.presentation.dto;

import FIS.iLUVit.domain.presentation.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PresentationForTeacherResponse {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private List<String> presentationInfoImage;
    private boolean periodValid;

    public static PresentationForTeacherResponse of(Presentation presentation, List<String> presentationInfoImages){
        return PresentationForTeacherResponse.builder()
                .presentationId(presentation.getId())
                .startDate(presentation.getStartDate())
                .endDate(presentation.getEndDate())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .presentationInfoImage(presentationInfoImages)
                .periodValid(LocalDate.now().isBefore(presentation.getEndDate()))
                .build();

    }
}
