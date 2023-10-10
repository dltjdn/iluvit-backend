package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CenterMapResponse {
    private Long id;
    private String name;                    // 시설명
    private Double longitude;               // 경도
    private Double latitude;                // 위도
    private Boolean signed;                 // 원장 가입 유무

    public static CenterMapResponse from(Center center){
        return CenterMapResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .longitude(center.getLongitude())
                .latitude(center.getLatitude())
                .signed(center.getSigned())
                .build();
    }

}
