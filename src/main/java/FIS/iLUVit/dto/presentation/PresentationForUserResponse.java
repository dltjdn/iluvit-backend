package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresentationForUserResponse {
    private Long centerId;
    private String centerName;
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Theme theme;
    private List<String> infoImages = new ArrayList<>();

    private boolean periodValid;

    public PresentationForUserResponse(PresentationForUserDto dto,List<String> infoImages){
        this.centerId = dto.getCenterId();
        this.centerName = dto.getCenterName();
        this.presentationId = dto.getPresentationId();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.place = dto.getPlace();
        this.content = dto.getContent();
        this.theme = dto.getTheme();
        this.infoImages = infoImages;
        this.periodValid = !LocalDate.now().isAfter(endDate);
    }
}
