package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.Center;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CenterFindForUserResponse {
    private Long id;
    private String name;
    private String address;

    public CenterFindForUserResponse(Center center){
        this.id = center.getId();
        this.name = center.getName();
        this.address = center.getAddress();
    }
}
