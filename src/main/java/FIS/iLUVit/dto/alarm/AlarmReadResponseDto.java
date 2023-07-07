package FIS.iLUVit.dto.alarm;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlarmReadResponseDto {
    private String message;
    private Integer status;
    private Boolean success;
    private Boolean data;
}
