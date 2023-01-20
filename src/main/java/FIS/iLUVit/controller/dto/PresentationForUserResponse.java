package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.PresentationForUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
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

    public PresentationForUserResponse(PresentationForUserDto dto){
        this.centerId = dto.getCenterId();
        this.centerName = dto.getCenterName();
        this.presentationId = dto.getPresentationId();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.place = dto.getPlace();
        this.content = dto.getContent();
        this.theme = dto.getTheme();
    }
}
