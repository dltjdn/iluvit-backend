package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpoTokenInfo {
    private Long id;
    private String token;
    private Boolean accept;
}
