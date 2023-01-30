package FIS.iLUVit.dto.center;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CenterRequest {
    private String sido;
    private String sigungu;
    private String centerName;
}
