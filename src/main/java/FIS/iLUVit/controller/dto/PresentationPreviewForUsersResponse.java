package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.PresentationPreviewForUsers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentationPreviewForUsersResponse {
    private Long centerId;
    private String centerName;
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Theme theme;
    private List<String> infoImages = new ArrayList<>();

    public PresentationPreviewForUsersResponse(PresentationPreviewForUsers dto){
        this.centerId = centerId;
        this.centerName = centerName;
        this.presentationId = presentationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.content = content;
        this.theme = theme;
    }
}
