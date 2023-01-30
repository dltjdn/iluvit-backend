package FIS.iLUVit.dto.presentation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationRequest {
    @NotNull
    private Long presentationId;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate startDate;          // 설명회 신청 기간

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;

    @NotNull
    private String place;               // 설명회 장소
    @NotNull
    private String content;             // 설명회 내용

    @Size(min = 1, message = "설명회 작성 미완료")
    @NotNull
    private List<PtDateDto> ptDateDtos = new ArrayList<>();
}
