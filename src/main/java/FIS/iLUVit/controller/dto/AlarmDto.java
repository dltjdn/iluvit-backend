package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmDto {
    protected String message;
    protected String type;
}
