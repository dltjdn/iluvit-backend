package FIS.iLUVit.domain.ptdate.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PtDateDto {
    private Long ptDateId;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate date;

    @NotNull
    private String time;            // 설명회 날짜 시간

    @NotNull
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
}
