package FIS.iLUVit.domain.expotoken.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ExpoTokenDeviceIdRequest {
    @NotNull
    private String deviceId;
}