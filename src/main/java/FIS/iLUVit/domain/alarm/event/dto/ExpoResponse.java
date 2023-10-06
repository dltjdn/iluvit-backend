package FIS.iLUVit.domain.alarm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpoResponse {
    private String status;
    private String id;
    private String message;
    private ExpoDetailDto expoDetailDto;
}