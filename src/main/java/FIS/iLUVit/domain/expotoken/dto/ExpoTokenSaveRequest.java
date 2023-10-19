package FIS.iLUVit.domain.expotoken.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ExpoTokenSaveRequest {

    @NotNull(message = "토큰 값을 정확히 넣어주세요.")
    private String token;
    @NotNull(message = "동의 여부에는 true/false 만 허용합니다. null 값 X")
    private Boolean accept;
    @NotNull(message = "디바이스 아이디 값을 정확히 넣어주세요.")
    private String deviceId;

}
