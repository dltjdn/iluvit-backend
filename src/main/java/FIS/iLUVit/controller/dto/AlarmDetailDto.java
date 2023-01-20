package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AlarmDetailDto {
    protected Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd, hh:mm:ss")
    protected LocalDateTime localDateTime;

    protected String message;

    protected String type;
}
