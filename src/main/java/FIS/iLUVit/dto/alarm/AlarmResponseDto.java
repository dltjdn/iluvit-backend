package FIS.iLUVit.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmResponseDto {
    private String message;
    private Integer status;
    private Boolean success;

}

