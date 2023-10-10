package FIS.iLUVit.domain.center.dto;

import FIS.iLUVit.domain.center.domain.KindOf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class CenterMapFilterRequest {
    @NotNull
    private KindOf kindOf;
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    private List<Long> centerIds;

}
