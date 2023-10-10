package FIS.iLUVit.domain.alarm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpoDetailDto {
    private String error;
    private String expoPushToken;
}