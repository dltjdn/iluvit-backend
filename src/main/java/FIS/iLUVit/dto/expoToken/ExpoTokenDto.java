package FIS.iLUVit.dto.expoToken;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpoTokenDto {
    private Long id;
    private String token;
    private Boolean accept;
}
