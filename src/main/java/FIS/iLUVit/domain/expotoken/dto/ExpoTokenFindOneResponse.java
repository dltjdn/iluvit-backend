package FIS.iLUVit.domain.expotoken.dto;

import FIS.iLUVit.domain.expotoken.domain.ExpoToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpoTokenFindOneResponse {
    private Long id;
    private String token;
    private String deviceId;
    private Boolean active;
    private Boolean accept;

    public ExpoTokenFindOneResponse(ExpoToken expoToken) {
        this.id = expoToken.getId();
        this.token = expoToken.getToken();
        this.deviceId = expoToken.getDeviceId();
        this.active = expoToken.getActive();
        this.accept = expoToken.getAccept();
    }

}
