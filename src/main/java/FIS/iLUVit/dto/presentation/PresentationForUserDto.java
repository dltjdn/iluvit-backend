package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.embeddable.Theme;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresentationForUserDto {

    private Long centerId;
    private String centerName;
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Theme theme;                // 센터 테마
    private String centerAddress;   // 센터 주소 ( 시/군/구 )
    private String infoImages;

    @QueryProjection
    public PresentationForUserDto(Presentation presentation, Center center) {
        centerId = center.getId();
        centerName = center.getName();
        presentationId = presentation.getId();
        startDate = presentation.getStartDate();
        endDate = presentation.getEndDate();
        place = presentation.getPlace();
        content = presentation.getContent();
        theme = center.getTheme();
        centerAddress = center.getArea().getSigungu();
        infoImages = presentation.getInfoImagePath();
    }
}
