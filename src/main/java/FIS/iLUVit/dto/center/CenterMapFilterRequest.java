package FIS.iLUVit.dto.center;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CenterMapFilterRequest {
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;     // 시설종류
    private List<Long> centerIds = new ArrayList<>();

}
