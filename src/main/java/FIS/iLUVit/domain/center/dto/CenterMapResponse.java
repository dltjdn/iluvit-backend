package FIS.iLUVit.domain.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterMapResponse {
    private Long id;
    private String name;                    // 시설명
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private Boolean signed;                 // 원장 가입 유무

}
