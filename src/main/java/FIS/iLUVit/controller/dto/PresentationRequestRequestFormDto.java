package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Presentation;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class PresentationRequestRequestFormDto {
    private Long centerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;          // 설명회 신청 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용

    @Size(min = 1, message = "설명회 작성 미완료")
    private List<PtDateRequestDto> ptDateDtos;

    public static Presentation toPresentation(PresentationRequestRequestFormDto request){
        return Presentation.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .place(request.getPlace())
                .build();
    }
}
