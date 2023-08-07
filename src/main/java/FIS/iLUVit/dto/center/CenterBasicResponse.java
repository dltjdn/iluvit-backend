package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
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
