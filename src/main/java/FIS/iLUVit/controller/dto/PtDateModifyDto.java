package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PtDateModifyDto {
    private Long ptDateId;
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private String time;            // 설명회 날짜 시간
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
}
