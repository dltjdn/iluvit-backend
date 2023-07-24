package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CenterDto {
    private Long id;
    private String name;
    private String address;

    @QueryProjection
    public CenterDto(Long id, String centerName, String centerAddress) {
        this.id = id;
        this.name = centerName;
        this.address = centerAddress;
    }

    public CenterDto (Center center){
        this.id = center.getId();
        this.name = center.getName();
        this.address = center.getAddress();
    }
}
