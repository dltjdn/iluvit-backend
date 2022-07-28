package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.exception.PresentationException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentationRequestRequestFormDto {
    @NotNull
    private Long centerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "설명회 신청 시작일자를 작성해주세요")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;          // 설명회 신청 기간


    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "설명회 신청 종료일자를 작성해주세요")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;


    @NotNull(message = "설명회 장소을 작성해주세요")
    private String place;               // 설명회 장소
    @NotNull(message = "설명회 내용을 작성해주세요")
    private String content;             // 설명회 내용

    @Size(min = 1, message = "설명회 작성 미완료")
    @NotNull(message = "설명회 작성 미완료")
    private List<PtDateRequestDto> ptDateDtos;

    public static Presentation toPresentation(PresentationRequestRequestFormDto request){
        if(request.endDate.isBefore(request.startDate))
            throw new PresentationException("시작일자와 종료일자를 다시 확인해 주세요.");
        return Presentation.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .place(request.getPlace())
                .build();
    }
}
