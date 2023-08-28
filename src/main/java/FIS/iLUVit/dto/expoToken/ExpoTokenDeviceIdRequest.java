package FIS.iLUVit.dto.expoToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpoTokenDeviceIdRequest {
    @NotNull
    private String deviceId;
}