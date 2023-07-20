package FIS.iLUVit.dto.expoToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpoTokenDeviceIdDto {
    @NotNull
    private String deviceId;
}
