package FIS.iLUVit.dto.expoToken;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpoTokenDto {
    private Long id;
    private String token;
    private Boolean accept;
}
