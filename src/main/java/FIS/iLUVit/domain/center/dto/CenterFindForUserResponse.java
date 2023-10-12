package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CenterFindForUserResponse {
    private Long id;
    private String name;
    private String address;

    public static CenterFindForUserResponse from(Center center){
        return CenterFindForUserResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .address(center.getAddress())
                .build();
    }

}
