package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentationForUserResponse {
    private Long centerId;
    private String centerName;
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private List<String> infoImages = new ArrayList<>();
    private boolean periodValid;
    private String centerAddress;        // 센터 지번 주소 중 시/군/구
    private Theme theme;                // 센터 테마


    public static PresentationForUserResponse of(Presentation presentation, List<String> infoImages){
        return PresentationForUserResponse.builder()
                .centerId(presentation.getCenter().getId())
                .centerName(presentation.getCenter().getName())
                .presentationId(presentation.getId())
                .startDate(presentation.getStartDate())
                .endDate(presentation.getEndDate())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .infoImages(infoImages)
                .periodValid(LocalDate.now().isBefore(presentation.getEndDate()))
                .centerAddress(presentation.getCenter().getAddress())
                .theme(presentation.getCenter().getTheme())
                .build();

    }
}
