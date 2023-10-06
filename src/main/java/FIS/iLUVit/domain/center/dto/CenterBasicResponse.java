package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CenterBasicResponse {
    private Long id;
    private String name;
    private String address;

    public CenterBasicResponse(Center center){
        this.id = center.getId();
        this.name = center.getName();
        this.address = center.getAddress();
    }
}
