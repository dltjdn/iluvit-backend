package FIS.iLUVit.dto.expoToken;

import FIS.iLUVit.domain.ExpoToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpoTokenDto {
    private Long id;
    private String token;
    private String deviceId;
    private Boolean active;
    private Boolean accept;

    public ExpoTokenDto(ExpoToken expoToken) {
        this.id = expoToken.getId();
        this.token = expoToken.getToken();
        this.deviceId = expoToken.getDeviceId();
        this.active = expoToken.getActive();
        this.accept = expoToken.getAccept();
    }
}



