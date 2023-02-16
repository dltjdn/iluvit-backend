package FIS.iLUVit.dto.center;

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
}
